package com.liux.android.downloader.core;

import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.storage.Record;

/**
 * 任务实例抽象类
 */
public abstract class Task implements TaskInfo, TaskOperational {

    private Record record;

    Task(Record record) {
        this.record = record;
    }

    Record getRecord() {
        return record;
    }

    public abstract long getID();

    public abstract void bindStatusListener(OnStatusListener onStatusListener);

    public abstract void unbindStatusListener(OnStatusListener onStatusListener);
}
