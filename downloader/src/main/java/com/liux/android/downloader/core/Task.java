package com.liux.android.downloader.core;

import com.liux.android.downloader.OnStatusListener;

/**
 * 任务实例抽象接口
 */
public interface Task extends TaskInfo, TaskOperational {

    void bindStatusListener(OnStatusListener onStatusListener);

    void unbindStatusListener(OnStatusListener onStatusListener);
}
