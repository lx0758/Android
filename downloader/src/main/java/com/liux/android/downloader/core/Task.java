package com.liux.android.downloader.core;

public interface Task extends TaskInfo, TaskHandle, TaskListener {

    long getTaskID();
}
