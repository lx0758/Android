package com.liux.android.downloader;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.liux.android.downloader.core.Task;

public abstract class UIStatusListener extends OnStatusListener {

    private Handler handler;

    public UIStatusListener() {
        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Task task = (Task) msg.obj;
                onUIUpdate(task);
                return true;
            }
        });
    }

    @Override
    public void onUpdate(final Task task) {
        Message message = handler.obtainMessage();
        message.obj = task;
        handler.sendMessage(
                message
        );
    }

    protected abstract void onUIUpdate(Task task);
}
