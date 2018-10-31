package com.liux.android.downloader;

import com.liux.android.downloader.core.DownloaderService;
import com.liux.android.downloader.core.Task;

import java.util.List;

/**
 * 下载器封装工具类
 */
public class Downloader {

    public static boolean isInit() {
        return DownloaderService.isInit();
    }

    public static void init(Config config) {
        DownloaderService.init(config);
    }

    public static Task createTask(String url) {
        return createTaskBuilder(url).build();
    }

    public static TaskBuilder createTaskBuilder(String url) {
        return new TaskBuilder(url);
    }

    public static Task getTask(long taskId) {
        return DownloaderService.get().getTask(taskId);
    }

    public static List<Task> getAllTasks() {
        return DownloaderService.get().getAllTasks();
    }

    public static void startAllTasks() {
        DownloaderService.get().startAllTasks();
    }

    public static void stopAllTasks() {
        DownloaderService.get().stopAllTasks();
    }

    public static void resetAllTasks() {
        DownloaderService.get().resetAllTasks();
    }

    public static void deleteAllTasks() {
        DownloaderService.get().deleteAllTasks();
    }

    public static void registerTaskCallback(DownloaderCallback downloaderCallback) {
        DownloaderService.get().registerTaskCallback(downloaderCallback);
    }

    public static void unregisterTaskCallback(DownloaderCallback downloaderCallback) {
        DownloaderService.get().unregisterTaskCallback(downloaderCallback);
    }
}
