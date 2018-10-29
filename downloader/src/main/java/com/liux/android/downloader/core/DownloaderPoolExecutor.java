package com.liux.android.downloader.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class DownloaderPoolExecutor extends ThreadPoolExecutor {

    private Collection<DownloaderRunnable> collection = new LinkedList<>();

    DownloaderPoolExecutor(int maxTaskCount, BlockingQueue<Runnable> blockingQueue) {
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
        if (r instanceof DownloaderRunnable) collection.add((DownloaderRunnable) r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (collection.contains(r)) collection.remove(r);
    }

    @Override
    protected void terminated() {
        super.terminated();
        collection.clear();
    }

    protected Collection<DownloaderRunnable> getRunningRunnables() {
        return collection;
    }
}
