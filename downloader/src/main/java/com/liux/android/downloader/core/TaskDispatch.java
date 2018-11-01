package com.liux.android.downloader.core;

/**
 * 任务调度器抽象接口
 */
interface TaskDispatch {

    /**
     * 开始一个任务
     * @param downloaderTask
     */
    void start(DownloaderTask downloaderTask);

    /**
     * 停止一个任务
     * @param downloaderTask
     */
    void stop(DownloaderTask downloaderTask);

    /**
     * 重置一个任务
     * @param downloaderTask
     */
    void reset(DownloaderTask downloaderTask);

    /**
     * 删除一个任务
     * @param downloaderTask
     */
    void delete(DownloaderTask downloaderTask);
}
