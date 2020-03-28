package com.liux.android.http.request;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Response;

/**
 * 2018/4/10
 * By Liux
 * lx0758@qq.com
 */
public abstract class UICallback implements Callback {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onFailure(final Request request, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onUIFailure(request, e);
            }
        });
    }

    protected abstract void onUISucceed(Request request, Response response) throws IOException;

    protected abstract void onUIFailure(Request request, IOException e);

    @Override
    public void onSucceed(final Request request, final Response response) throws IOException {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    onUISucceed(request, response);
                } catch (IOException e) {
                    onFailure(request, e);
                }
            }
        });
    }
}
