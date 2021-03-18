package com.liux.android.downloader;

import com.liux.android.downloader.core.Task;

/**
 * 任务状态监听器
 */
public abstract class OnStatusListener {

    private Task task;

    /**
     * 绑定监听器之后的回调
     * @param task
     */
    public final void onBind(Task task) {
        if (this.task != null && this.task != task) {
            this.task.unbindStatusListener(this);
        }
        this.task = task;
        onUpdate(task);
    }

    /**
     * 状态变更之后的回调
     * @param task
     */
    public abstract void onUpdate(Task task);
}
