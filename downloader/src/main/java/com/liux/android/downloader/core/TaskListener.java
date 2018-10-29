package com.liux.android.downloader.core;

import com.liux.android.downloader.OnStatusListener;

public interface TaskListener {

    void bindStatusListener(OnStatusListener onStatusListener);

    void unbindStatusListener(OnStatusListener onStatusListener);
}
