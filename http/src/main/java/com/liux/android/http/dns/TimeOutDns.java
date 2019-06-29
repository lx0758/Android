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

    private long timeOutMills = 5000;

    public TimeOutDns() {

    }

    public TimeOutDns(int time, TimeUnit timeUnit) {
        timeOutMills = timeUnit.toMillis(time);
    }

    @Override
    public List<InetAddress> lookup(final String hostname) throws UnknownHostException {
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
            throw unknownHostException;
        }
    }
}
