package com.liux.android.downloader;

import com.liux.android.downloader.core.Task;

/**
 * 任务状态监听器
 */
public interface OnStatusListener {

    /**
     * 绑定监听器之后的回调
     * @param task
     */
    void onBind(Task task);

    /**
     * 状态变更之后的回调
     * @param task
     */
    void onUpdate(Task task);
}
