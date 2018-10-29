package com.liux.android.downloader;

import com.liux.android.downloader.core.DownloaderProxy;
import com.liux.android.downloader.core.Task;

import java.util.List;
import java.util.Map;

public class Downloader {

    public static void init(Config config) {
        DownloaderProxy.get().init(config);
    }

    public static Task createTask(String url) {
        return createTask(url, "GET", null);
    }

    public static Task createTask(String url, String method) {
        return createTask(url, method, null);
    }

    public static Task createTask(String url, Map<String, List<String>> header) {
        return createTask(url, "GET", header);
    }

    public static Task createTask(String url, String method, Map<String, List<String>> header) {
        return DownloaderProxy.get().createTask(url, method, header);
    }

    public static Task getTask(long taskId) {
        return DownloaderProxy.get().getTask(taskId);
    }

    public static List<Task> getAllTasks() {
        return DownloaderProxy.get().getAllTasks();
    }

    public static void startAllTasks() {
        DownloaderProxy.get().startAllTasks();
    }

    public static void stopAllTasks() {
        DownloaderProxy.get().stopAllTasks();
    }

    public static void resetAllTasks() {
        DownloaderProxy.get().resetAllTasks();
    }

    public static void deleteAllTasks() {
        DownloaderProxy.get().deleteAllTasks();
    }

    public static void registerTaskCallback(DownloaderCallback downloaderCallback) {
        DownloaderProxy.get().registerTaskCallback(downloaderCallback);
    }

    public static void unregisterTaskCallback(DownloaderCallback downloaderCallback) {
        DownloaderProxy.get().unregisterTaskCallback(downloaderCallback);
    }
}
