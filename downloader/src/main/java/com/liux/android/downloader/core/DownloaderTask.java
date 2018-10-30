package com.liux.android.downloader.core;

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
class DownloaderTask implements Task, Runnable {

    DownloaderTask(Record record, File rootDirectory, DataStorage dataStorage, FileStorage fileStorage, ConnectFactory connectFactory, TaskDispatch taskDispatch, DownloaderCallback downloaderCallback) {

    }

    @Override
    public void bindStatusListener(OnStatusListener onStatusListener) {

    }

    @Override
    public void unbindStatusListener(OnStatusListener onStatusListener) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void delete() {

    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getMethod() {
        return null;
    }

    @Override
    public Map<String, List<Status>> getHeaders() {
        return null;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public long getCompleted() {
        return 0;
    }

    @Override
    public long getTotal() {
        return 0;
    }

    @Override
    public long getSpeed() {
        return 0;
    }

    @Override
    public Status getStatus() {
        return null;
    }

    @Override
    public Date getCreateTime() {
        return null;
    }

    @Override
    public Date getUpdateTime() {
        return null;
    }

    @Override
    public void run() {

    }
}
