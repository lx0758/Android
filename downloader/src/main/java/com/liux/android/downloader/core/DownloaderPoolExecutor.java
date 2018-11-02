package com.liux.android.downloader.core;

import com.liux.android.downloader.Downloader;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 下载任务线程池
 */
class DownloaderPoolExecutor extends ThreadPoolExecutor {

    // 正在执行队列
    private BlockingQueue<Runnable> runnableBlockingQueue;
    // 等待执行队列
    private BlockingQueue<DownloaderTask> downloaderTaskBlockingQueue;

    static DownloaderPoolExecutor create(int maxTaskCount) {
        return new DownloaderPoolExecutor(maxTaskCount, new LinkedBlockingQueue<Runnable>());
    }

    private DownloaderPoolExecutor(int maxTaskCount, BlockingQueue<Runnable> blockingQueue) {
        super(
                maxTaskCount,
                2^6,
                500,
                TimeUnit.MILLISECONDS,
                blockingQueue,
                new DownloaderThreadFactory()
        );
        runnableBlockingQueue = blockingQueue;
        downloaderTaskBlockingQueue = new LinkedBlockingQueue<>();
    }

    /**
     * 提交一个下载任务
     * @param downloaderTask
     * @return
     */
    public void submitTask(DownloaderTask downloaderTask) {
        // 如果存在于在工作任务队列,返回
        if (runnableBlockingQueue.contains(downloaderTask)) return;
        // 如果存在于在等待任务队列且不是单一任务,返回
        if (downloaderTaskBlockingQueue.contains(downloaderTask) && !downloaderTask.getSingle()) return;

        // 如果当前未达到线程池设置数量或者该任务是单任务,则直接提交.否则添加入等待执行队列
        if (getActiveCount() < getCorePoolSize() || downloaderTask.getSingle()) {
            submitDownloaderTask(downloaderTask);
        } else {
            downloaderTaskBlockingQueue.add(downloaderTask);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        DownloaderTask downloaderTask = downloaderTaskBlockingQueue.poll();
        if (downloaderTask != null) {
            submitDownloaderTask(downloaderTask);
        }
    }

    /**
     * 提交一个下载任务,并将控制器还给下载任务
     * @param downloaderTask
     */
    private void submitDownloaderTask(DownloaderTask downloaderTask) {
        Future future = submit(downloaderTask);
        downloaderTask.setFuture(future);
    }

    private static class DownloaderThreadFactory implements ThreadFactory {
        private final AtomicInteger atomicInteger = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, Downloader.TAG + " #" + atomicInteger.getAndIncrement());
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}
