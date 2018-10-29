package com.liux.android.downloader;

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
