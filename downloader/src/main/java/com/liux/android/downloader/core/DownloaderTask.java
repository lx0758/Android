package com.liux.android.downloader.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.liux.android.downloader.DownloaderCallback;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.Status;
import com.liux.android.downloader.network.Connect;
import com.liux.android.downloader.network.ConnectFactory;
import com.liux.android.downloader.network.ConnectResponse;
import com.liux.android.downloader.storage.DataStorage;
import com.liux.android.downloader.storage.FileStorage;
import com.liux.android.downloader.storage.Record;
import com.liux.android.downloader.storage.TempDataStorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 下载任务实例
 */
class DownloaderTask implements Runnable, Task, TaskInfoSeter {
    private static final int MSG_WHAT_CALL_UPDATE_TIMER = 400;

    private Record record;
    private DataStorage dataStorage;
    private FileStorage fileStorage;
    private ConnectFactory connectFactory;
    private TaskDispatch taskDispatch;
    private DownloaderCallback downloaderCallback;

    private Future future;
    private File writeFile;
    private Throwable errorInfo;
    private Handler callUpdateTimer;
    private List<OnStatusListener> onStatusListeners = new LinkedList<>();

    DownloaderTask(Record record, DataStorage dataStorage, FileStorage fileStorage, ConnectFactory connectFactory, TaskDispatch taskDispatch, DownloaderCallback downloaderCallback) {
        this.record = record;
        this.dataStorage = dataStorage;
        this.fileStorage = fileStorage;
        this.connectFactory = connectFactory;
        this.taskDispatch = taskDispatch;
        this.downloaderCallback = downloaderCallback;

        String fileName;
        if (TextUtils.isEmpty(record.getFileNameFinal())) {
            fileName = record.getFileName();
        } else {
            fileName = record.getFileNameFinal();
        }
        writeFile = new File(this.record.getDir(), fileName);

        callUpdateTimer = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                callStatusListenerUpdate();
                callUpdateTimer.sendEmptyMessageDelayed(MSG_WHAT_CALL_UPDATE_TIMER, 500);
                return true;
            }
        });
    }

    @Override
    public void run() {
        Connect connect = null;
        ConnectResponse connectResponse = null;
        RandomAccessFile randomAccessFile = null;
        try {
            if (Thread.currentThread().isInterrupted()) return;

            // 新任务初始化
            if (TextUtils.isEmpty(record.getFileNameFinal())) {
                int index = 1;
                String fileNameFinal = record.getFileName();
                while (fileStorage.isExist(record.getDir(), fileNameFinal)) {
                    fileNameFinal = getNewFileName(record.getFileName(), index);
                    index ++;
                }
                record.setFileNameFinal(fileNameFinal);
                dataStorage.onUpdate(record);

                writeFile = new File(record.getDir(), record.getFileNameFinal());
            }

            // 获取连接器&打开存储文件
            connect = connectFactory.create();
            randomAccessFile = fileStorage.onOpen(record.getDir(), record.getFileNameFinal());

            // 探测资源
            setStatus(Status.CONN);
            // 是否需要从头开始下载
            boolean needRestartDownload = false;
            // 处理探测 headers,加入检测断点续传参数
            Map<String, List<String>> connectHeaders = getHeaders();
            if (connectHeaders == null) connectHeaders = new HashMap<>();
            connectHeaders.put("Range", Collections.singletonList("bytes=0-"));
            // 发起探测连接,如果返回405不则支持HEAD请求,再使用设置的请求方法再请求一次
            connectResponse = connect.connect(getUrl(), "HEAD", connectHeaders, false);
            if (Thread.currentThread().isInterrupted()) return;
            connectResponse.close();
            if (connectResponse.code() == 405) {
                connectResponse = connect.connect(getUrl(), getMethod(), connectHeaders, false);
                if (Thread.currentThread().isInterrupted()) return;
                connectResponse.close();
            }
            if (!connectResponse.isSuccessful()) throw new ConnectException(connectResponse.message());
            // 是否支持续传,不支持则重新下载
            if (!connectResponse.hasHeader("content-range") && !connectResponse.hasHeader("accept-ranges")) {
                needRestartDownload = true;
            }
            // 获取&校验文件长度,如果文件长度发生变化则重新下载
            long total = 0;
            if (connectResponse.hasHeader("content-length")) {
                String value = connectResponse.header("content-length").get(0);
                total = Long.valueOf(value);
            }
            if (record.getTotal() != total) {
                needRestartDownload = true;
            }
            record.setTotal(total);
            // 获取&校验缓存标志,如果标志发生变化则重新下载
            String etag = null;
            if (connectResponse.hasHeader("etag")) {
                etag = connectResponse.header("etag").get(0);
            }
            if (!TextUtils.isEmpty(etag)) {
                needRestartDownload = !etag.equals(record.getEtag());
            } else if (!TextUtils.isEmpty(record.getEtag())){
                needRestartDownload = true;
            }
            record.setEtag(etag);
            // 检测文件和数据库记录是否一致,如果小于记录则以文件为准
            if (randomAccessFile.length() < record.getCompleted()) {
                // 按照文件调整已完成进度
                record.setCompleted(randomAccessFile.length());
                // 抛出错误 或者 重新开始下载
                //throw new IOException("File status and database record status are inconsistent");
                //needRestartDownload = true;
                // 同步处理速度缓存
                speedLastSize = record.getCompleted();
                speedLastTime = System.currentTimeMillis();
            }
            // 如果文件比总大小还大则重新下载,相同则完成下载
            if (record.getTotal() > 0) {
                if (randomAccessFile.length() > record.getTotal()) {
                    needRestartDownload = true;
                } else if (randomAccessFile.length() == record.getTotal()) {
                    setStatus(Status.COMPLETE);
                    return;
                }
            }
            // 更新探测数据
            dataStorage.onUpdate(record);

            if (Thread.currentThread().isInterrupted()) return;

            // 下载资源
            setStatus(Status.START);
            // 处理下载 headers,加入断点续传参数
            Map<String, List<String>> downloadHeaders = getHeaders();
            if (downloadHeaders == null) downloadHeaders = new HashMap<>();
            if (needRestartDownload) {
                record.setCompleted(0);
                dataStorage.onUpdate(record);
                callStatusListenerUpdate();
                randomAccessFile.seek(0);
                downloadHeaders.put("Range", Collections.singletonList("bytes=0-"));
                // 同步处理速度缓存
                speedLastSize = record.getCompleted();
                speedLastTime = System.currentTimeMillis();
            } else {
                randomAccessFile.seek(record.getCompleted());
                downloadHeaders.put("Range", Collections.singletonList("bytes=" + String.valueOf(record.getCompleted()) + "-"));
            }
            // 发起下载连接
            connectResponse = connect.connect(getUrl(), getMethod(), downloadHeaders, true);
            if (Thread.currentThread().isInterrupted()) return;
            if (!connectResponse.isSuccessful()) throw new ConnectException(connectResponse.message());
            // 开始传输
            InputStream inputStream = connectResponse.inputstream();
            int length;
            long cacheLength = 0;
            byte[] bytes = new byte[10240];
            while ((length = inputStream.read(bytes)) != -1) {
                randomAccessFile.write(bytes, 0, length);
                record.setCompleted(record.getCompleted() + length);

                // 更新数据库,为了减少 IO 读写频率这里设置了一个缓存阈值
                cacheLength += length;
                if (cacheLength >= 5 * 10240) {
                    dataStorage.onUpdate(record);
                    cacheLength = 0;
                }

                // 如果状态已经不是已下载,则退出线程
                if (getStatus() != Status.START) {
                    dataStorage.onUpdate(record);
                    return;
                }
            }
            if (Thread.currentThread().isInterrupted()) return;
            dataStorage.onUpdate(record);

            // 如果下载流程走完已下载大小不等于探测结果则抛错
            if (record.getTotal() > 0 && record.getCompleted() < record.getTotal()) {
                throw new ConnectException("download unexpectedly stopped");
            }

            // 设置状态为完成
            setStatus(Status.COMPLETE);
        } catch (Exception e) {
            if (e.getClass().equals(InterruptedException.class) ||
                    e.getClass().equals(InterruptedIOException.class)) {
                return;
            }
            errorInfo = e;
            setStatus(Status.ERROR);
        } finally {
            if (connect != null) connect.close();
            if (connectResponse != null) connectResponse.close();
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException ignore) {}
            }
        }
    }

    @Override
    public void bindStatusListener(OnStatusListener onStatusListener) {
        if (onStatusListener == null) return;
        if (onStatusListeners.contains(onStatusListener)) return;
        onStatusListeners.add(onStatusListener);
        onStatusListener.onBind(this);
    }

    @Override
    public void unbindStatusListener(OnStatusListener onStatusListener) {
        if (onStatusListener == null) return;
        onStatusListeners.remove(onStatusListener);
    }

    @Override
    public boolean isStarted() {
        Status status = getStatus();
        return status == Status.WAIT ||
                status == Status.CONN ||
                status == Status.START;
    }

    @Override
    public boolean isFailed() {
        Status status = getStatus();
        return status == Status.ERROR;
    }

    @Override
    public boolean isDeleted() {
        Status status = getStatus();
        return status == Status.DELETE;
    }

    @Override
    public boolean isCompleted() {
        Status status = getStatus();
        return status == Status.COMPLETE;
    }

    @Override
    public void start() {
        taskDispatch.start(this);
    }

    @Override
    public void stop() {
        taskDispatch.stop(this);
    }

    @Override
    public void reset() {
        taskDispatch.reset(this);
    }

    @Override
    public void delete() {
        taskDispatch.delete(this);
    }

    @Override
    public File syncStart() throws IOException, IllegalStateException {
        switch (getStatus()) {
            case NEW:
            case STOP:
            case ERROR:
                run();
                if (isCompleted()) throw new IOException(getErrorInfo());
                return getFile();
            default:
                throw new IllegalStateException("Status=" + getStatus() + ", the download task currently cannot synchronous start. because not\'s NEW or STOP or ERROR");
        }
    }

    @Override
    public long getId() {
        return record.getId();
    }

    @Override
    public String getUrl() {
        return record.getUrl();
    }

    @Override
    public String getMethod() {
        return record.getMethod();
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return DownloaderUtil.json2headers(record.getHeaders());
    }

    @Override
    public File getFile() {
        return writeFile;
    }

    @Override
    public long getCompleted() {
        return record.getCompleted();
    }

    @Override
    public long getTotal() {
        return record.getTotal();
    }

    private long speedLast, speedLastSize, speedLastTime;
    @Override
    public long getSpeed() {
        if (isCompleted()) return speedLast;
        if (!isStarted()) return 0;

        // 第一次统计
        if (speedLastSize <= 0 || speedLastTime <= 0) {
            speedLastSize = getCompleted();
            speedLastTime = System.currentTimeMillis();
            return 0;
        }

        long size = getCompleted();;
        long time = System.currentTimeMillis();

        // 异常原因导致统计错误
        long differenceSize = size - speedLastSize;
        long differenceTime = time - speedLastTime;
        if (differenceSize <= 0 || differenceTime <= 0) return 0;

        // 防止间隔时间太短造成统计失真
        if (differenceTime < 1000) return speedLast;

        // 单位时间内下载量 * 1秒出现间隔时间的倍数
        // differenceSize * (1000.0f / differenceTime) => differenceSize * 1000.0f / differenceTime
        speedLast = (long) (differenceSize * 1000.0f / differenceTime);
        speedLastSize = size;
        speedLastTime = time;

        return speedLast;
    }

    @Override
    public synchronized Status getStatus() {
        return Status.codeOf(record.getStatus());
    }

    @Override
    public Date getCreateTime() {
        return new Date(record.getCreateTime());
    }

    @Override
    public Date getUpdateTime() {
        return new Date(record.getCreateTime());
    }

    @Override
    public boolean getTemporary() {
        return dataStorage instanceof TempDataStorage;
    }

    @Override
    public Throwable getErrorInfo() {
        return errorInfo;
    }

    @Override
    public synchronized void setStatus(Status status) {
        if (status == null) return;

        Status oldStatus = getStatus();
        // 如果切换状态和当前任务相同,则直接返回
        if (oldStatus == status) return;
        // 如果任务已经完成,过滤掉一部分操作
        if (oldStatus == Status.COMPLETE && (
                //status == Status.NEW ||
                status == Status.WAIT ||
                status == Status.CONN ||
                status == Status.START ||
                status == Status.STOP ||
                status == Status.ERROR
                //status == Status.COMPLETE ||
                //status == Status.DELETE
        )) return;
        // 如果任务已经是删除状态,则不允许在操作
        if (oldStatus == Status.DELETE) return;
        // 取消错误状态时清除错误信息
        if (status != Status.ERROR) errorInfo = null;
        // 切换到开始状态时启动定时任务
        if (status == Status.START) {
            startCallUpdateTimer();
        } else {
            stopCallUpdateTimer();
        }

        switch (status) {
            case NEW:
                // 虽然是新建,但这里的作用是重置(恢复到新建的状态).真正的新建逻辑在Service中处理
                checkAndCancelTask(true);
                if (!TextUtils.isEmpty(record.getFileNameFinal())) {
                    fileStorage.onDelete(record.getDir(), record.getFileNameFinal());
                }
                record.setEtag(null);
                record.setCreateTime(0);
                record.setTotal(0);
                record.setFileNameFinal(null);
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                downloaderCallback.onTaskReset(this);
                break;
            case WAIT:
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                break;
            case CONN:
                downloaderCallback.onTaskStarted(this);
            case START:
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                break;
            case STOP:
                checkAndCancelTask(true);
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                downloaderCallback.onTaskStopped(this);
                break;
            case ERROR:
                checkAndCancelTask(false);
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                downloaderCallback.onTaskFailed(this, errorInfo);
                break;
            case COMPLETE:
                checkAndCancelTask(false);
                if (record.getTotal() == 0) {
                    record.setTotal(record.getCompleted());
                }
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                downloaderCallback.onTaskCompleted(this);
                break;
            case DELETE:
                checkAndCancelTask(true);
                if (!TextUtils.isEmpty(record.getFileNameFinal())) {
                    fileStorage.onDelete(record.getDir(), record.getFileNameFinal());
                }
                record.setStatus(status.code());
                dataStorage.onDelete(record);
                downloaderCallback.onTaskDeleted(this);
                break;
        }
        callStatusListenerUpdate();
    }

    @Override
    public void setFuture(Future future) {
        this.future = future;
    }

    /**
     * 根据原始文件名获取新的文件名
     * temp        => temp(i)
     * temp.tar.gz => temp.tar(i).gz
     * @param fileName
     * @param index
     * @return
     */
    private String getNewFileName(String fileName, int index) {
        int pos = fileName.lastIndexOf(".");
        if (pos == -1) return fileName + "(" + index + ")";

        fileName = fileName.substring(0, pos) + "(" + index + ")" + fileName.substring(pos);

        return fileName;
    }

    /**
     * 回调所有状态监听器
     */
    private void callStatusListenerUpdate() {
        for (OnStatusListener onStatusListener : onStatusListeners) {
            onStatusListener.onUpdate(this);
        }
    }

    /**
     * 检查并且取消任务
     * @param force 是否强制停止, 因为 ERROR 和 COMPLETE 都会正常退出线程,所以不需要中断退出线程
     */
    private void checkAndCancelTask(boolean force) {
        if (force && future != null && !future.isDone() && !future.isCancelled()) {
            future.cancel(true);
        }
        future = null;

        taskDispatch.removeForWait(this);
    }

    /**
     * 启动定时更新
     */
    private void startCallUpdateTimer() {
        callUpdateTimer.sendEmptyMessage(MSG_WHAT_CALL_UPDATE_TIMER);
    }

    /**
     * 停止定时更新
     */
    private void stopCallUpdateTimer() {
        callUpdateTimer.removeMessages(MSG_WHAT_CALL_UPDATE_TIMER);
    }
}
