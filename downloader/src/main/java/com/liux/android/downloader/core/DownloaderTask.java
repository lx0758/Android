package com.liux.android.downloader.core;

import com.liux.android.downloader.DownloaderCallback;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.Status;
import com.liux.android.downloader.network.ConnectFactory;
import com.liux.android.downloader.storage.DataStorage;
import com.liux.android.downloader.storage.FileStorage;
import com.liux.android.downloader.storage.Record;

import java.io.File;

/**
 * 下载任务实例
 */
class DownloaderTask extends Task implements Runnable {

    DownloaderTask(Record record, File rootDirectory, DataStorage dataStorage, FileStorage fileStorage, ConnectFactory connectFactory, TaskDispatch taskDispatch, DownloaderCallback downloaderCallback) {
        super(record);
    }

    @Override
    public void run() {

    }

    @Override
    public long getID() {
        return 0;
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
    public Status getStatus() {
        return null;
    }

    @Override
    public void bindStatusListener(OnStatusListener onStatusListener) {

    }

    @Override
    public void unbindStatusListener(OnStatusListener onStatusListener) {

    }
}
