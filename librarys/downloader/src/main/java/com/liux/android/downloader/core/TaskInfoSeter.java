package com.liux.android.downloader.core;

import com.liux.android.downloader.Status;

import java.util.concurrent.Future;

interface TaskInfoSeter {

    void setStatus(Status status);

    void setFuture(Future future);
}
