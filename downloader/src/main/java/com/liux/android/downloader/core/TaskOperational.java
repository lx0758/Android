package com.liux.android.downloader.core;

/**
 * 任务操作接口
 */
interface TaskOperational {

    /**
     * 开始自己
     */
    void start();

    /**
     * 停止自己
     */
    void stop();

    /**
     * 重置自己
     */
    void reset();

    /**
     * 删除自己
     */
    void delete();
}
