package com.liux.android.downloader.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 下载任务线程池
 */
class DownloaderPoolExecutor extends ThreadPoolExecutor {

    private Collection<DownloaderTask> runningTasks = new LinkedList<>();

    public DownloaderPoolExecutor(int maxTaskCount, BlockingQueue<Runnable> blockingQueue) {
        super(
                maxTaskCount,
                maxTaskCount,
                0,
                TimeUnit.MILLISECONDS,
                blockingQueue
        );
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (r instanceof DownloaderTask) runningTasks.add((DownloaderTask) r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        runningTasks.remove(r);
    }

    @Override
    protected void terminated() {
        super.terminated();
        runningTasks.clear();
    }

    protected Collection<DownloaderTask> getRunningTasks() {
        return runningTasks;
    }
}
