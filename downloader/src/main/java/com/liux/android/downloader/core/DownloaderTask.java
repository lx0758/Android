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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 下载任务实例
 */
class DownloaderTask implements Runnable, Task, TaskInfoSeter {
    private static final int MSG_CALL_UPDATE_TIMER_WHAT = 100;

    private Record record;
    private DataStorage dataStorage;
    private FileStorage fileStorage;
    private ConnectFactory connectFactory;
    private TaskDispatch taskDispatch;
    private DownloaderCallback downloaderCallback;

    private File writeFile;
    private Throwable errorInfo;
    private Handler callUpdateTimer;
    private List<WeakReference<OnStatusListener>> onStatusListeners = new LinkedList<>();

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
        writeFile = new File(this.record.getDir() + File.separator + fileName);

        callUpdateTimer = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                callStatusListenerUpdate();
                callUpdateTimer.sendEmptyMessageDelayed(MSG_CALL_UPDATE_TIMER_WHAT, 500);
                return true;
            }
        });
    }

    @Override
    public void run() {
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

            writeFile = new File(record.getDir() + File.separator + record.getFileNameFinal());
        }
        callStatusListenerUpdate();

        Connect connect = null;
        ConnectResponse connectResponse = null;
        RandomAccessFile randomAccessFile = null;
        try {
            // 获取一个连接器
            connect = connectFactory.create();
            // 打开文件
            randomAccessFile = fileStorage.onOpen(record.getDir(), record.getFileNameFinal());

            // 探测资源
            setStatus(Status.CONN);
            // 是否需要从头开始下载(不支持断点续传,etag发生变更的情况)
            boolean needRestart = false;
            Map<String, List<String>> connectHeaders = getHeaders();
            if (connectHeaders == null) connectHeaders = new HashMap<>();
            // 探测是否支持断点续传
            connectHeaders.put("Range", Collections.singletonList("bytes=0-"));
            // 发起探测连接
            connectResponse = connect.connect(getUrl(), "HEAD", connectHeaders, false);
            // 检查是否成功
            if (!connectResponse.isSuccessful()) throw new ConnectException();
            // 获取是否支持续传
            if (!connectResponse.hasHeader("content-range") && !connectResponse.hasHeader("accept-ranges")) {
                needRestart = true;
            }
            // 获取内容长度
            if (connectResponse.hasHeader("content-length")) {
                String value = connectResponse.header("content-length").get(0);
                record.setTotal(Long.valueOf(value));
            }
            // 获取&校验缓存标志
            if (connectResponse.hasHeader("etag")) {
                String value = connectResponse.header("etag").get(0);
                if (!TextUtils.isEmpty(record.getEtag()) && !record.getEtag().equals(value)) {
                    needRestart = true;
                }
                record.setEtag(value);
            }
            connectResponse.close();
            dataStorage.onUpdate(record);

            // 下载资源
            setStatus(Status.START);
            // 检测文件和数据库记录是否一致
            if (randomAccessFile.length() < record.getCompleted()) {
                throw new IOException("文件状态和数据库记录状态不一致");
            }
            // 处理下载 headers,加入断点续传参数
            Map<String, List<String>> downloadHeaders = getHeaders();
            if (downloadHeaders == null) downloadHeaders = new HashMap<>();
            if (needRestart) {
                record.setCompleted(0);
                randomAccessFile.seek(0);
                callStatusListenerUpdate();
                downloadHeaders.put("Range", Collections.singletonList("bytes=0-"));
            } else {
                randomAccessFile.seek(record.getCompleted());
                downloadHeaders.put("Range", Collections.singletonList("bytes=" + String.valueOf(record.getCompleted()) + "-"));
            }
            // 发起下载连接
            connectResponse = connect.connect(getUrl(), getMethod(), downloadHeaders, true);
            // 检查是否成功
            if (!connectResponse.isSuccessful()) throw new ConnectException();
            // 开始传输
            InputStream inputStream = connectResponse.inputstream();
            int length;
            long cacheLength = 0;
            byte[] bytes = new byte[10240];
            while ((length = inputStream.read(bytes)) != -1) {
                if (getStatus() != Status.START) break;
                randomAccessFile.write(bytes, 0, length);
                record.setCompleted(record.getCompleted() + length);

                // 更新数据库,为了减少 IO 读写频率这里设置了一个阈值
                cacheLength += length;
                if (cacheLength >= 5 * 10240) {
                    dataStorage.onUpdate(record);
                    cacheLength = 0;
                }
            }
            dataStorage.onUpdate(record);

            setStatus(Status.COMPLETE);
        } catch (Exception e) {
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
        if (findOnStatusListener(onStatusListener) != null) return;
        onStatusListeners.add(new WeakReference<>(onStatusListener));
        onStatusListener.onBind(this);
    }

    @Override
    public void unbindStatusListener(OnStatusListener onStatusListener) {
        if (onStatusListener == null) return;
        Iterator iterator;
        if ((iterator = findOnStatusListener(onStatusListener)) != null) iterator.remove();
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
        // 第一次统计
        if (speedLastSize <= 0 || speedLastTime <= 0) {
            speedLastSize = getCompleted();
            speedLastTime = System.currentTimeMillis();
            return 0;
        }

        // 异常原因导致统计错误
        long differenceSize = getCompleted() - speedLastSize;
        long differenceTime = System.currentTimeMillis() - speedLastTime;
        if (differenceSize <= 0 || differenceTime <= 0) return 0;

        // 防止间隔时间太短造成统计失真
        if (differenceTime < 500) return speedLast;

        // 单位时间内下载量 * 1秒出现间隔时间的倍数
        speedLast = (long) (differenceSize * (1000.0f / differenceTime));
        speedLastSize = getCompleted();
        speedLastTime = System.currentTimeMillis();

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
    public Throwable getErrorInfo() {
        return errorInfo;
    }

    @Override
    public synchronized void setStatus(Status status) {
        if (status == null) return;
        Status oldStatus = getStatus();
        if (oldStatus == status || oldStatus == Status.DELETE) return;

        if (status == Status.ERROR) errorInfo = null;
        if (status == Status.START) {
            startCallUpdateTimer();
        } else {
            stopCallUpdateTimer();
        }

        switch (status) {
            case NEW:
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                downloaderCallback.onTaskCreated(this);
                break;
            case WAIT:
                downloaderCallback.onTaskStarted(this);
            case CONN:
            case START:
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                break;
            case STOP:
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                downloaderCallback.onTaskStopped(this);
                break;
            case ERROR:
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                downloaderCallback.onTaskFailed(this, errorInfo);
                break;
            case COMPLETE:
                record.setStatus(status.code());
                dataStorage.onUpdate(record);
                downloaderCallback.onTaskCompleted(this);
                break;
            case DELETE:
                record.setStatus(status.code());
                dataStorage.onDelete(record);
                fileStorage.onDelete(record.getDir(), record.getFileNameFinal());
                downloaderCallback.onTaskDeleted(this);
                break;
        }
        callStatusListenerUpdate();
    }

    /**
     * 在弱引用集合内查找持有某个 listener 的弱引用
     * @param listener
     * @return
     */
    private Iterator findOnStatusListener(OnStatusListener listener) {
        Iterator<WeakReference<OnStatusListener>> iterator = onStatusListeners.iterator();
        while (iterator.hasNext()) {
            OnStatusListener onStatusListener = iterator.next().get();
            if (onStatusListener == null) {
                iterator.remove();
                continue;
            }
            if (listener == onStatusListener) return iterator;
        }
        return null;
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
        Iterator<WeakReference<OnStatusListener>> iterator = onStatusListeners.iterator();
        while (iterator.hasNext()) {
            OnStatusListener onStatusListener = iterator.next().get();
            if (onStatusListener == null) {
                iterator.remove();
                continue;
            }
            onStatusListener.onUpdate(this);
        }
    }

    /**
     * 启动定时更新
     */
    private void startCallUpdateTimer() {
        callUpdateTimer.sendEmptyMessage(MSG_CALL_UPDATE_TIMER_WHAT);
    }

    /**
     * 停止定时更新
     */
    private void stopCallUpdateTimer() {
        callUpdateTimer.removeMessages(MSG_CALL_UPDATE_TIMER_WHAT);
    }
}
