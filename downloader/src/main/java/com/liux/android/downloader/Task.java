package com.liux.android.downloader;

public interface Task extends TaskHandle, TaskStatus {

    long getTaskID();
}
