package com.liux.android.downloader;

import com.liux.android.downloader.core.DownloaderCreator;
import com.liux.android.downloader.core.DownloaderService;
import com.liux.android.downloader.core.Task;

import java.util.List;

/**
 * 下载器封装工具类
 */
public class Downloader {
    public static final String TAG = "[Downloader]";

    /**
     * 是否已经初始化
     * @return
     */
    public static boolean isInit() {
        return DownloaderCreator.isInit();
    }

    /**
     * 初始化下载器
     * @param config
     */
    public static void init(Config config) {
        DownloaderCreator.init(config);
    }

    /**
     * 注册初始化回调接口
     * @param initCallback
     */
    public static void registerInitCallback(InitCallback initCallback) {
        DownloaderCreator.registerInitCallback(initCallback);
    }

    /**
     * 取消注册初始化回调接口
     * @param initCallback
     */
    public static void unregisterInitCallback(InitCallback initCallback) {
        DownloaderCreator.unregisterInitCallback(initCallback);
    }

    /**
     * 创建任务
     * @param url
     * @return
     */
    public static Task createTask(String url) {
        return createTaskBuilder(url).build();
    }

    /**
     * 创建临时任务
     * @param url
     * @return
     */
    public static Task createTemporaryTask(String url) {
        return createTaskBuilder(url).temporary(true).build();
    }

    /**
     * 创建任务建造者
     * @param url
     * @return
     */
    public static TaskBuilder createTaskBuilder(String url) {
        return new TaskBuilder(url);
    }

    /**
     * 获取一个任务
     * @param taskId
     * @return
     */
    public static Task getTask(long taskId) {
        return DownloaderService.get().getTask(taskId);
    }

    /**
     * 获取所有任务
     * @return
     */
    public static List<Task> getAllTasks() {
        return DownloaderService.get().getAllTasks();
    }

    /**
     * 开始所有任务
     */
    public static void startAllTasks() {
        DownloaderService.get().startAllTasks();
    }

    /**
     * 停止所有任务
     */
    public static void stopAllTasks() {
        DownloaderService.get().stopAllTasks();
    }

    /**
     * 删除所有任务
     */
    public static void deleteAllTasks() {
        DownloaderService.get().deleteAllTasks();
    }

    /**
     * 注册任务回调接口
     * @param downloaderCallback
     */
    public static void registerTaskCallback(DownloaderCallback downloaderCallback) {
        DownloaderService.get().registerTaskCallback(downloaderCallback);
    }

    /**
     * 取消注册任务回调接口
     * @param downloaderCallback
     */
    public static void unregisterTaskCallback(DownloaderCallback downloaderCallback) {
        DownloaderService.get().unregisterTaskCallback(downloaderCallback);
    }
}
