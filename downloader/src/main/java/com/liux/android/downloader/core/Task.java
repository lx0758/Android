package com.liux.android.downloader.core;

import com.liux.android.downloader.OnStatusListener;

/**
 * 任务实例抽象接口
 */
public interface Task extends TaskStatus, TaskInfoGeter, TaskOperational {

    /**
     * 绑定任务状态监听器回调
     * @param onStatusListener
     */
    void bindStatusListener(OnStatusListener onStatusListener);

    /**
     * 解除绑定任务状态监听器回调
     * @param onStatusListener
     */
    void unbindStatusListener(OnStatusListener onStatusListener);
}
