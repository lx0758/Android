package com.liux.android.downloader;

/**
 * 任务状态监听器
 */
public interface OnStatusListener {

    /**
     * 绑定监听器之后的回调
     */
    void onBind();

    /**
     * 状态变更之后的回调
     */
    void onUpdate();
}
