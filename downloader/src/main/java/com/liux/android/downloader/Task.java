package com.liux.android.downloader;

public interface Task extends TaskStatus, TaskHandle, TaskListener {

    long getTaskID();
}
