package com.liux.android.downloader.core;

/**
 * 任务调度器抽象接口
 */
public interface TaskDispatch {

    /**
     * 开始一个任务
     * @param task
     */
    void start(Task task);

    /**
     * 停止一个任务
     * @param task
     */
    void stop(Task task);

    /**
     * 重置一个任务
     * @param task
     */
    void reset(Task task);

    /**
     * 删除一个任务
     * @param task
     */
    void delete(Task task);
}
