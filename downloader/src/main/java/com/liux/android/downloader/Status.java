package com.liux.android.downloader;

public enum  Status {

    /**
     * 未知状态
     */
    NONE,

    /**
     * 等待状态
     */
    WAIT,

    /**
     * 连接状态
     */
    CONN,

    /**
     * 下载状态
     */
    START,

    /**
     * 停止状态
     */
    STOP,

    /**
     * 出错状态
     *
     */
    ERROR,

    /**
     * 完成状态
     */
    COMPLETE
}
