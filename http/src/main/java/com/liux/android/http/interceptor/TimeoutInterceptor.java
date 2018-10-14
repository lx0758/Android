package com.liux.android.http.interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 2018/4/3
 * By Liux
 * lx0758@qq.com
 */
public class TimeoutInterceptor implements Interceptor {
    // 自定义头部信息,标记连接超时
    public static final String HEADER_TIMEOUT_CONNECT = "Timeout-Connect";
    // 自定义头部信息,标记写超时
    public static final String HEADER_TIMEOUT_WRITE = "Timeout-Write";
    // 自定义头部信息,标记读超时
    public static final String HEADER_TIMEOUT_READ = "Timeout-Read";

    private int mOverallConnectTimeout, mOverallWriteTimeout, mOverallReadTimeout;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        int connectTimeout, writeTimeout, readTimeout;

        connectTimeout = mOverallConnectTimeout;
        writeTimeout = mOverallWriteTimeout;
        readTimeout = mOverallReadTimeout;

        connectTimeout = parseTimeout(request, HEADER_TIMEOUT_CONNECT, connectTimeout);
        writeTimeout = parseTimeout(request, HEADER_TIMEOUT_WRITE, writeTimeout);
        readTimeout = parseTimeout(request, HEADER_TIMEOUT_READ, readTimeout);

        if (connectTimeout > 0 && connectTimeout != chain.connectTimeoutMillis() / 1000) {
            chain = chain.withConnectTimeout(connectTimeout, TimeUnit.SECONDS);
        }
        if (writeTimeout > 0 && writeTimeout != chain.writeTimeoutMillis() / 1000) {
            chain = chain.withWriteTimeout(writeTimeout, TimeUnit.SECONDS);
        }
        if (readTimeout > 0 && readTimeout != chain.readTimeoutMillis() / 1000) {
            chain = chain.withReadTimeout(readTimeout, TimeUnit.SECONDS);
        }

        request = request.newBuilder()
                .removeHeader(HEADER_TIMEOUT_CONNECT)
                .removeHeader(HEADER_TIMEOUT_WRITE)
                .removeHeader(HEADER_TIMEOUT_READ)
                .build();

        return chain.proceed(request);
    }

    public int getOverallConnectTimeout() {
        return mOverallConnectTimeout;
    }

    public void setOverallConnectTimeout(int overallConnectTimeout) {
        mOverallConnectTimeout = overallConnectTimeout;
    }

    public int getOverallWriteTimeout() {
        return mOverallWriteTimeout;
    }

    public void setOverallWriteTimeout(int overallWriteTimeout) {
        mOverallWriteTimeout = overallWriteTimeout;
    }

    public int getOverallReadTimeout() {
        return mOverallReadTimeout;
    }

    public void setOverallReadTimeout(int overallReadTimeout) {
        mOverallReadTimeout = overallReadTimeout;
    }

    private int parseTimeout(Request request, String header, int defaultValue) {
        String timeString = request.header(header);
        if (TextUtils.isEmpty(timeString)) return defaultValue;
        if (!timeString.matches("^\\d{1,10}$")) return defaultValue;
        int time = Integer.valueOf(timeString);
        if (time > 0) return time;
        return defaultValue;
    }
}
