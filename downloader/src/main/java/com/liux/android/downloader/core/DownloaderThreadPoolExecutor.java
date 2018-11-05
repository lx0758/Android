package com.liux.android.downloader.core;

import android.util.Log;

import com.liux.android.downloader.Downloader;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 下载任务线程池
 * 1.对于可以等待的任务,线程池只会使用核心线程执行,核心线程数达到设定值后,
 *   任务会被外部队列存储等待线程池执行
 * 2.对于需要立即运行的临时任务,如果有空闲状态的核心线程,则使用该核心线程运行
 *   如果提交时没有空闲才核心线程且核心线程数已经达到设定值,则会启动非核心线程运行,
 *   临时任务执行完毕之后,线程池不会回收掉该非核心线程不再复用
 */
class DownloaderThreadPoolExecutor extends ThreadPoolExecutor {

    // 正在执行集合
    private Collection<DownloaderTask> runningCollection;;
    // 等待执行队列
    private BlockingQueue<DownloaderTask> waitingBlockingQueue;

    static DownloaderThreadPoolExecutor create(int maxTaskCount) {
        return new DownloaderThreadPoolExecutor(
                maxTaskCount,
                new DownloaderBlockingQueue()
        );
    }

    private DownloaderThreadPoolExecutor(int maxTaskCount, DownloaderBlockingQueue downloaderBlockingQueue) {
        super(
                maxTaskCount,
                2^6,
                500,
                TimeUnit.MILLISECONDS,
                downloaderBlockingQueue,
                new DownloaderThreadFactory(),
                new DownloaderRejectedExecutionHandler()
        );
        runningCollection = new LinkedList<>();
        waitingBlockingQueue = downloaderBlockingQueue.getWaitingBlockingQueue();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (r instanceof DownloaderFutureTask) {
            runningCollection.add(((DownloaderFutureTask) r).getDownloaderTask());
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (r instanceof DownloaderFutureTask) {
            runningCollection.remove(((DownloaderFutureTask) r).getDownloaderTask());
        }
    }

    /**
     * 提交一个下载任务
     * @param downloaderTask
     * @return
     */
    public void submitTask(DownloaderTask downloaderTask) {
        // 如果存在于在工作任务队列,返回
        if (runningCollection.contains(downloaderTask)) return;
        // 如果存在于在等待任务队列,返回
        if (waitingBlockingQueue.contains(downloaderTask)) return;

        // 如果当前核心线程数未达到线程池设置数量或者该任务是单任务,则直接提交.否则添加入等待执行队列
        // 由于修改了队列实现,核心线程数超过配置值后提交的任务都直接启动非核心线程执行
        if (getPoolSize() < getCorePoolSize() || downloaderTask.getTemporary()) {
            // 有核心线程并且处于空闲状态则使用核心线程,否则使用非核心线程
            if (getActiveCount() < getPoolSize()) {
                waitingBlockingQueue.add(downloaderTask);
            } else {
                executeTask(downloaderTask);
            }
        } else {
            waitingBlockingQueue.add(downloaderTask);
        }
    }

    /**
     * 从等待任务队列移除一个下载任务
     * @param downloaderTask
     */
    public void cancelTask(DownloaderTask downloaderTask) {
        waitingBlockingQueue.remove(downloaderTask);
    }

    /**
     * 执行一个下载任务,并将控制器还给下载任务
     * @param downloaderTask
     */
    private void executeTask(DownloaderTask downloaderTask) {
        DownloaderFutureTask downloaderFutureTask = new DownloaderFutureTask(downloaderTask);
        downloaderTask.setFuture(downloaderFutureTask);
        execute(downloaderFutureTask);
    }

    /**
     * 下载器任务队列,永远是满状态但实际内容为空的伪阻塞队列
     */
    private static class DownloaderBlockingQueue extends LinkedBlockingQueue<Runnable> {

        private BlockingQueue<DownloaderTask> waitingBlockingQueue;

        public DownloaderBlockingQueue() {
            super();
            waitingBlockingQueue = new LinkedBlockingQueue<>();
        }

        public BlockingQueue<DownloaderTask> getWaitingBlockingQueue() {
            return waitingBlockingQueue;
        }

        /**
         * 添加一个元素并返回true
         * 如果队列已满，则返回false
         * 调用 {@link #execute} 时通过这个方法检测队列是否已满
         * @param runnable
         * @return
         */
        @Override
        public boolean offer(Runnable runnable) {
            return false;
        }

        /**
         * 移除并返问队列头部的元素
         * 如果队列为空，则返回null
         * 非核心线程任务结束时会调用,返回null线程池会认为没有多余任务,而将非核心线程销毁
         * @param timeout
         * @param unit
         * @return
         * @throws InterruptedException
         */
        @Override
        public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
            return null;
        }

        /**
         * 移除并返回队列头部的元素
         * 如果队列为空，则阻塞
         * 核心线程任务结束时会调用
         * @return
         * @throws InterruptedException
         */
        @Override
        public Runnable take() throws InterruptedException {
            return waitingBlockingQueue.take();
        }
    }

    /**
     * 下载器线程池线程工厂
     */
    private static class DownloaderThreadFactory implements ThreadFactory {
        private final AtomicInteger atomicInteger = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, Downloader.TAG + " #" + atomicInteger.getAndIncrement());
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }

    /**
     * 下载器线程池拒绝处理器
     */
    private static class DownloaderRejectedExecutionHandler implements RejectedExecutionHandler {

        public DownloaderRejectedExecutionHandler() {
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Log.e(Downloader.TAG, "Task " + r.toString() + " rejected from " + executor.toString());
        }
    }

    /**
     * 下载器线程池包装 FutureTask
     */
    private static class DownloaderFutureTask extends FutureTask {

        private DownloaderTask downloaderTask;

        public DownloaderFutureTask(DownloaderTask downloaderTask) {
            super(downloaderTask, null);
            this.downloaderTask = downloaderTask;
        }

        public DownloaderTask getDownloaderTask() {
            return downloaderTask;
        }
    }
}
