package com.liux.android.downloader.core;

/**
 * 任务状态获取接口
 */
interface TaskStatus {

    /**
     * 获取任务是否在运行中
     * @return
     */
    boolean isStarted();

    /**
     * 任务是否已经失败
     * @return
     */
    boolean isFailed();

    /**
     * 任务是否已删除
     * @return
     */
    boolean isDeleted();

    /**
     * 任务是否已经完成
     * @return
     */
    boolean isCompleted();
}
