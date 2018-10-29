package com.liux.android.downloader.core;

public interface TaskHandle {

    void start();

    void stop();

    void reset();

    void delete();
}
