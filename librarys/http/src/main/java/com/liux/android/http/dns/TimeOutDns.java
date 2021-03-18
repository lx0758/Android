package com.liux.android.http.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;

public class TimeOutDns implements Dns {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    protected final long timeOutMills;
    protected final int maxRetryCount;

    public TimeOutDns() {
        this(500, TimeUnit.MILLISECONDS, 2);
    }

    public TimeOutDns(int time, TimeUnit timeUnit, int maxRetryCount) {
        long timeOutMills = timeUnit.toMillis(time);
        if (timeOutMills < 100) timeOutMills = 100;
        if (maxRetryCount < 1) maxRetryCount = 1;
        this.timeOutMills = timeOutMills;
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public List<InetAddress> lookup(final String hostname) throws UnknownHostException {
        UnknownHostException finalException = null;
        for (int i = 0; i < maxRetryCount; i++) {
            FutureTask<List<InetAddress>> futureTask = null;
            try {
                futureTask = new FutureTask<>(new Callable<List<InetAddress>>() {
                    @Override
                    public List<InetAddress> call() throws Exception {
                        return SYSTEM.lookup(hostname);
                    }
                });
                EXECUTOR.execute(futureTask);
                return futureTask.get(timeOutMills, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                if (futureTask != null) futureTask.cancel(true);
                UnknownHostException unknownHostException = new UnknownHostException("Broken system behaviour for dns lookup of " + hostname);
                unknownHostException.initCause(e);
                finalException = unknownHostException;
            }
        }
        assert finalException != null;
        throw finalException;
    }
}
