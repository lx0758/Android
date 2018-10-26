package com.liux.android.downloader;

public interface TaskStatus {

    void bindStatusListener(OnStatusListener onStatusListener);

    void unbindStatusListener(OnStatusListener onStatusListener);
}
