package com.liux.android.downloader.core;

import com.liux.android.downloader.Config;
import com.liux.android.downloader.DownloaderCallback;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloaderProxy {
    private static volatile DownloaderProxy instance;

    public static DownloaderProxy get() {
        if (instance == null) throw new NullPointerException("DownloaderProxy has not been initialized");
        return instance;
    }
    public static void init(Config config) {
        if (instance != null) return;
        synchronized(DownloaderProxy.class) {
            if (instance != null) return;
            instance = new DownloaderProxy(config);
        }
    }

    private Config config;
    private BlockingQueue<Runnable> blockingQueue;
    private DownloaderPoolExecutor threadPoolExecutor;
    private List<DownloaderCallback> downloaderCallbacks;

    private DownloaderProxy(Config config) {
        this.config = config;
        this.downloaderCallbacks = new LinkedList<>();
        this.blockingQueue = new LinkedBlockingQueue<>();
        this.threadPoolExecutor = new DownloaderPoolExecutor(config.getMaxTaskCount(), blockingQueue);
    }

    public Task createTask(String url, String method, Map<String, List<String>> header) {
        return null;
    }

    public Task getTask(long taskId) {
        return null;
    }

    public List<Task> getAllTasks() {
        return null;
    }

    public void startAllTasks() {

    }

    public void stopAllTasks() {

    }

    public void resetAllTasks() {

    }

    public void deleteAllTasks() {

    }

    public void registerTaskCallback(DownloaderCallback downloaderCallback) {
        if (downloaderCallbacks.contains(downloaderCallback)) return;
        downloaderCallbacks.add(downloaderCallback);
    }

    public void unregisterTaskCallback(DownloaderCallback downloaderCallback) {
        downloaderCallbacks.remove(downloaderCallback);
    }
}
