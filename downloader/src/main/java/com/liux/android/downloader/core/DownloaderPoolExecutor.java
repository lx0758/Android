package com.liux.android.downloader.core;

import com.liux.android.downloader.Downloader;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 下载任务线程池
 */
class DownloaderPoolExecutor extends ThreadPoolExecutor {

    DownloaderPoolExecutor(int maxTaskCount, BlockingQueue<Runnable> blockingQueue) {
        super(
                maxTaskCount,
                maxTaskCount,
                0,
                TimeUnit.MILLISECONDS,
                blockingQueue,
                new DownloaderThreadFactory()
        );
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {
        super.terminated();
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
