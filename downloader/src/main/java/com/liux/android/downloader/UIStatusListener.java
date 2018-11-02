package com.liux.android.downloader;

import android.os.Handler;
import android.os.Looper;

import com.liux.android.downloader.core.Task;

public abstract class UIStatusListener implements OnStatusListener {

    private Handler handler;

    public UIStatusListener() {
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onBind(final Task task) {
        onUpdate(task);
    }

    @Override
    public void onUpdate(final Task task) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onUIUpdate(task);
            }
        });
    }

    protected abstract void onUIUpdate(Task task);
}
