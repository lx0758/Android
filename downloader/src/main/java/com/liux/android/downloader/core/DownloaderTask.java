package com.liux.android.downloader.core;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.DownloaderCallback;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.Status;
import com.liux.android.downloader.network.ConnectFactory;
import com.liux.android.downloader.storage.DataStorage;
import com.liux.android.downloader.storage.FileStorage;
import com.liux.android.downloader.storage.Record;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 下载任务实例
 */
class DownloaderTask implements Task, TaskInfoSeter, Runnable {

    private Record record;
    private File rootDirectory;
    private DataStorage dataStorage;
    private FileStorage fileStorage;
    private ConnectFactory connectFactory;
    private TaskDispatch taskDispatch;
    private DownloaderCallback downloaderCallback;

    private File writeFile;

    public DownloaderTask(Record record, File rootDirectory, DataStorage dataStorage, FileStorage fileStorage, ConnectFactory connectFactory, TaskDispatch taskDispatch, DownloaderCallback downloaderCallback) {
        this.record = record;
        this.rootDirectory = rootDirectory;
        this.dataStorage = dataStorage;
        this.fileStorage = fileStorage;
        this.connectFactory = connectFactory;
        this.taskDispatch = taskDispatch;
        this.downloaderCallback = downloaderCallback;
    }

    @Override
    public void bindStatusListener(OnStatusListener onStatusListener) {

    }

    @Override
    public void unbindStatusListener(OnStatusListener onStatusListener) {

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
    public Status getStatus() {
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
    public void setStatus(Status status) {

    }

    @Override
    public void run() {
        System.out.println(Downloader.TAG + "Thread " + Thread.currentThread().getName() + " Running...");
    }
}
