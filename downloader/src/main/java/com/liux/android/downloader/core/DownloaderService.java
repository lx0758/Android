package com.liux.android.downloader.core;

import android.text.TextUtils;

import com.liux.android.downloader.Config;
import com.liux.android.downloader.DownloaderCallback;
import com.liux.android.downloader.Status;
import com.liux.android.downloader.storage.Record;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 下载库类核心类
 */
public class DownloaderService implements TaskDispatch {
    private static volatile DownloaderService instance;
    public static DownloaderService get() {
        if (instance == null) throw new NullPointerException("DownloaderService has not been initialized");
        return instance;
    }
    public static boolean isInit() {
        return instance != null;
    }
    public static void init(Config config) {
        if (instance != null) return;
        synchronized(DownloaderService.class) {
            if (instance != null) return;
            instance = new DownloaderService(config);
        }
    }

    private DownloaderPoolExecutor threadPoolExecutor;

    private Config config;
    private List<Task> tasks;
    private DownloaderCallbackDispense downloaderCallbackDispense;

    private DownloaderService(Config config) {
        this.config = config;
        this.tasks = new LinkedList<>();
        this.downloaderCallbackDispense = new DownloaderCallbackDispense();

        this.threadPoolExecutor = new DownloaderPoolExecutor(config.getMaxTaskCount(), new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public void start(Task task) {

    }

    @Override
    public void stop(Task task) {

    }

    @Override
    public void reset(Task task) {

    }

    @Override
    public void delete(Task task) {

    }

    /**
     * 创建一个任务,相同参数重复调用会产生多个任务
     * @param url
     * @param method
     * @param headers
     * @param dir 自定义存储目录,若为 null,则使用全局配置
     * @param fileName 自定义文件名.若为null,则下载链接时自动配置
     * @return
     */
    public Task createTask(String url, String method, Map<String, List<String>> headers, File dir, String fileName) {
        if (TextUtils.isEmpty(url)) throw new NullPointerException();

        if (TextUtils.isEmpty(method)) method = "GET";
        method = method.toUpperCase();

        if (dir == null) dir = config.getRootDirectory();

        if (TextUtils.isEmpty(fileName)) {
            int begin = url.lastIndexOf('/') + 1;
            int end = url.length();
            int find = -1;
            if ((find = url.indexOf("?", begin)) != -1) {
                end = find + 1;
            } else if ((find = url.indexOf("#", begin)) != -1) {
                end = find + 1;
            }
            fileName = url.substring(begin, end);
        }

        Record record = config.getDataStorage().onCreate(url, method, DownloaderUtil.headers2json(headers), dir.getAbsolutePath(), fileName, Status.NEW.code());

        DownloaderTask downloaderTask = createDownloaderTask(record, config, downloaderCallbackDispense);
        tasks.add(downloaderTask);

        return downloaderTask;
    }

    /**
     * 获取一个任务
     * @param taskId
     * @return
     */
    public Task getTask(long taskId) {
        for (Task task : tasks) {
            if (task.getId() == taskId) return task;
        }
        return null;
    }

    /**
     * 获取所有任务
     * @return
     */
    public List<Task> getAllTasks() {
        return tasks;
    }

    /**
     * 开始所有任务
     */
    public void startAllTasks() {

    }

    /**
     * 停止所有任务
     */
    public void stopAllTasks() {

    }

    /**
     * 重置所有任务
     */
    public void resetAllTasks() {

    }

    /**
     * 删除所有任务
     */
    public void deleteAllTasks() {

    }

    /**
     * 注册下载任务回调
     * @param downloaderCallback
     */
    public void registerTaskCallback(DownloaderCallback downloaderCallback) {
        downloaderCallbackDispense.add(downloaderCallback);
    }

    /**
     * 解除注册下载任务回调
     * @param downloaderCallback
     */
    public void unregisterTaskCallback(DownloaderCallback downloaderCallback) {
        downloaderCallbackDispense.remove(downloaderCallback);
    }

    /**
     * 构建一个任务实例
     * @param record
     * @param config
     * @param downloaderCallback
     * @return
     */
    private DownloaderTask createDownloaderTask(Record record, Config config, DownloaderCallback downloaderCallback) {
        return new DownloaderTask(
                record,
                config.getRootDirectory(),
                config.getDataStorage(),
                config.getFileStorage(),
                config.getConnectFactory(),
                this,
                downloaderCallback
        );
    }
}
