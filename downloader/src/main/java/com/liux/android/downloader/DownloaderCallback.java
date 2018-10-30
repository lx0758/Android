package com.liux.android.downloader;

import com.liux.android.downloader.core.Task;

/**
 * 下载器任务状态回调(主要用来处理关联业务)
 */
public interface DownloaderCallback {

    /**
     * 任务创建完毕
     * @param task
     */
    void onTaskCreated(Task task);

    /**
     * 任务已经开始
     * @param task
     */
    void onTaskStarted(Task task);

    /**
     * 任务已经停止
     * @param task
     */
    void onTaskStopped(Task task);

    /**
     * 任务已经重置
     * @param task
     */
    void onTaskReset(Task task);

    /**
     * 任务已经失败
     * @param task
     */
    void onTaskFailed(Task task);

    /**
     * 任务删除完毕
     * @param task
     */
    void onTaskDeleted(Task task);

    /**
     * 任务完成
     * @param task
     */
    void onTaskCompleted(Task task);
}
