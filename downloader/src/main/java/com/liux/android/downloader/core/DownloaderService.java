package com.liux.android.downloader.core;

import android.text.TextUtils;

import com.liux.android.downloader.Config;
import com.liux.android.downloader.DownloaderCallback;
import com.liux.android.downloader.Status;
import com.liux.android.downloader.network.ConnectFactory;
import com.liux.android.downloader.storage.DataStorage;
import com.liux.android.downloader.storage.FileStorage;
import com.liux.android.downloader.storage.Record;
import com.liux.android.downloader.storage.TempDataStorage;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 下载库类核心类
 */
public class DownloaderService implements TaskDispatch {
    private static volatile DownloaderService downloaderService;
    static void setService(DownloaderService service) {
        downloaderService = service;
    }
    static DownloaderService getService() {
        return downloaderService;
    }
    public static DownloaderService get() {
        if (downloaderService == null) throw new NullPointerException("DownloaderService has not been initialized");
        return downloaderService;
    }

    private DownloaderThreadPoolExecutor downloaderThreadPoolExecutor;

    private Config config;
    private List<DownloaderTask> downloaderTasks;
    private DownloaderCallbackDispense downloaderCallbackDispense;

    DownloaderService(Config config) {
        this.config = config;
        this.downloaderTasks = new LinkedList<>();
        this.downloaderCallbackDispense = new DownloaderCallbackDispense();

        this.downloaderThreadPoolExecutor = DownloaderThreadPoolExecutor.create(config.getMaxTaskCount());

        config.getDataStorage().onInit(config.getContext());
        config.getFileStorage().onInit(config.getContext(), config.getDefaultDirectory());

        downloaderTasks.addAll(restoreTasksFromDataStorage(
                config,
                this,
                downloaderCallbackDispense
        ));

        schedulingTask();
    }

    @Override
    public void start(DownloaderTask downloaderTask) {
        if (downloaderTask == null) return;
        switch (downloaderTask.getStatus()) {
            case NEW:
            case STOP:
            case ERROR:
                downloaderTask.setStatus(Status.WAIT);
                schedulingTask();
                break;
            case WAIT:
            case CONN:
            case START:
            case COMPLETE:
            case DELETE:
                break;
        }
    }

    @Override
    public void stop(DownloaderTask downloaderTask) {
        downloaderTask.setStatus(Status.STOP);
    }

    @Override
    public void reset(DownloaderTask downloaderTask) {
        if (downloaderTask.getStatus() == Status.DELETE) return;
        downloaderTask.setStatus(Status.NEW);
    }

    @Override
    public void delete(DownloaderTask downloaderTask) {
        downloaderTask.setStatus(Status.DELETE);
        downloaderTasks.remove(downloaderTask);
    }

    @Override
    public void removeForWait(DownloaderTask downloaderTask) {
        downloaderThreadPoolExecutor.cancelTask(downloaderTask);
    }

    /**
     * 创建一个任务,相同参数重复调用会产生多个任务
     * @param url
     * @param method
     * @param headers
     * @param dir 自定义存储目录,若为 null,则使用全局配置
     * @param fileName 自定义文件名.若为null,则下载链接时自动配置
     * @param temporary
     * @return
     */
    public Task createTask(String url, String method, Map<String, List<String>> headers, File dir, String fileName, boolean temporary) {
        if (TextUtils.isEmpty(url)) throw new NullPointerException();

        if (TextUtils.isEmpty(method)) method = "GET";
        method = method.toUpperCase();

        if (dir == null) dir = !temporary ? config.getDefaultDirectory() : config.getTempDirectory();

        // 保证文件名不能为空
        // 首先尝试截取URL中的文件名,如果失败则设置默认值
        if (TextUtils.isEmpty(fileName)) {
            int begin = url.lastIndexOf('/') + 1;
            int end = url.length();
            int find = -1;
            if ((find = url.indexOf("?", begin)) != -1) {
                end = find;
            } else if ((find = url.indexOf("#", begin)) != -1) {
                end = find;
            }
            if (end > begin) {
                fileName = url.substring(begin, end);
            }
            if (TextUtils.isEmpty(fileName)) fileName = "file-" + System.currentTimeMillis();
        }

        Record record;
        DataStorage dataStorage;
        if (!temporary) {
            record = config.getDataStorage().onInsert(url, method, DownloaderUtil.headers2json(headers), dir.getAbsolutePath(), fileName, Status.NEW.code());
            dataStorage = config.getDataStorage();
        } else {
            record = Record.create(-1, url, method, DownloaderUtil.headers2json(headers), dir.getAbsolutePath(), fileName, Status.NEW.code(), System.currentTimeMillis());
            dataStorage = TempDataStorage.get();
        }

        DownloaderTask downloaderTask = createDownloaderTask(
                record,
                dataStorage,
                config.getFileStorage(),
                config.getConnectFactory(),
                this,
                downloaderCallbackDispense
        );
        downloaderTasks.add(downloaderTask);

        downloaderCallbackDispense.onTaskCreated(downloaderTask);

        return downloaderTask;
    }

    /**
     * 获取一个任务
     * @param taskId
     * @return
     */
    public Task getTask(long taskId) {
        for (Task task : downloaderTasks) {
            if (task.getId() == taskId) return task;
        }
        return null;
    }

    /**
     * 获取所有任务
     * @return
     */
    public List<Task> getAllTasks() {
        List<Task> tasks = new LinkedList<>();
        tasks.addAll(downloaderTasks);
        return tasks;
    }

    /**
     * 开始所有任务
     */
    public void startAllTasks() {
        for (DownloaderTask downloaderTask : downloaderTasks) {
            start(downloaderTask);
        }
        schedulingTask();
    }

    /**
     * 停止所有任务
     */
    public void stopAllTasks() {
        for (DownloaderTask downloaderTask : downloaderTasks) {
            Status status = downloaderTask.getStatus();
            if (status == Status.WAIT || status == Status.CONN || status == Status.START) {
                stop(downloaderTask);
            }
        }
    }

    /**
     * 删除所有任务
     */
    public void deleteAllTasks() {
        for (DownloaderTask downloaderTask : downloaderTasks) {
            Status status = downloaderTask.getStatus();
            if (status != Status.DELETE) {
                delete(downloaderTask);
            }
        }
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
     * 调度任务,扫描所有任务,将 {@link Status#WAIT} 的任务提交线程池
     */
    private synchronized void schedulingTask() {
        if (downloaderTasks.isEmpty()) return;

        for (DownloaderTask downloaderTask : downloaderTasks) {
            if (Status.WAIT != downloaderTask.getStatus()) continue;
            downloaderThreadPoolExecutor.submitTask(downloaderTask);
        }
    }

    /**
     * 从本地数据库中恢复任务，删除单任务只保留普通任务
     * 会将 {@link Status#CONN} 和 {@link Status#START} 的任务重置为 {@link Status#WAIT} 状态
     */
    private static List<DownloaderTask> restoreTasksFromDataStorage(Config config, TaskDispatch taskDispatch, DownloaderCallback downloaderCallback) {
        List<DownloaderTask> generalTasks = new LinkedList<>();

        List<Record> records = config.getDataStorage().onQueryAll();
        if (records == null) return generalTasks;

        for (Record record : records) {
            if (Status.WAIT.code() == record.getStatus() || Status.CONN.code() == record.getStatus() || Status.START.code() == record.getStatus()) {
                if (config.getRunUndoneForStart()) {
                    record.setStatus(Status.WAIT.code());
                } else {
                    record.setStatus(Status.STOP.code());
                }
            }
            DownloaderTask downloaderTask = createDownloaderTask(
                    record,
                    config.getDataStorage(),
                    config.getFileStorage(),
                    config.getConnectFactory(),
                    taskDispatch,
                    downloaderCallback
            );
            generalTasks.add(downloaderTask);
        }

        return generalTasks;
    }

    /**
     * 构建一个任务实例
     * @param record
     * @param dataStorage
     * @param fileStorage
     * @param connectFactory
     * @param taskDispatch
     * @param downloaderCallback
     * @return
     */
    private static DownloaderTask createDownloaderTask(Record record, DataStorage dataStorage, FileStorage fileStorage, ConnectFactory connectFactory, TaskDispatch taskDispatch, DownloaderCallback downloaderCallback) {
        return new DownloaderTask(
                record,
                dataStorage,
                fileStorage,
                connectFactory,
                taskDispatch,
                downloaderCallback
        );
    }
}
