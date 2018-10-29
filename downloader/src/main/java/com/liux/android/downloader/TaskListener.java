package com.liux.android.downloader;

public interface TaskListener {

    void bindStatusListener(OnStatusListener onStatusListener);

    void unbindStatusListener(OnStatusListener onStatusListener);
}
