package com.liux.android.downloader.core;

import com.liux.android.downloader.DownloaderCallback;

import java.util.LinkedList;
import java.util.List;

/**
 * 回调分发处理类
 */
public class DownloaderCallbackDispense implements DownloaderCallback {

    private List<DownloaderCallback> downloaderCallbacks = new LinkedList<>();

    @Override
    public void onTaskCreated(Task task) {
        for (DownloaderCallback downloaderCallback : downloaderCallbacks) {
            downloaderCallback.onTaskCreated(task);
        }
    }

    @Override
    public void onTaskStarted(Task task) {
        for (DownloaderCallback downloaderCallback : downloaderCallbacks) {
            downloaderCallback.onTaskStarted(task);
        }
    }

    @Override
    public void onTaskStopped(Task task) {
        for (DownloaderCallback downloaderCallback : downloaderCallbacks) {
            downloaderCallback.onTaskStopped(task);
        }
    }

    @Override
    public void onTaskReset(Task task) {
        for (DownloaderCallback downloaderCallback : downloaderCallbacks) {
            downloaderCallback.onTaskReset(task);
        }
    }

    @Override
    public void onTaskFailed(Task task) {
        for (DownloaderCallback downloaderCallback : downloaderCallbacks) {
            downloaderCallback.onTaskFailed(task);
        }
    }

    @Override
    public void onTaskDeleted(Task task) {
        for (DownloaderCallback downloaderCallback : downloaderCallbacks) {
            downloaderCallback.onTaskDeleted(task);
        }
    }

    @Override
    public void onTaskCompleted(Task task) {
        for (DownloaderCallback downloaderCallback : downloaderCallbacks) {
            downloaderCallback.onTaskCompleted(task);
        }
    }

    public void add(DownloaderCallback downloaderCallback) {
        if (downloaderCallbacks.contains(downloaderCallback)) return;
        downloaderCallbacks.add(downloaderCallback);
    }

    public void remove(DownloaderCallback downloaderCallback) {
        downloaderCallbacks.remove(downloaderCallback);
    }
}
