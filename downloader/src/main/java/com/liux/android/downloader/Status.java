package com.liux.android.downloader;

/**
 * 任务状态枚举
 */
public enum  Status {

    /**
     * 新建状态
     */
    NEW(0),

    /**
     * 等待状态
     */
    WAIT(10),

    /**
     * 连接状态
     */
    CONN(20),

    /**
     * 下载状态
     */
    START(30),

    /**
     * 停止状态
     */
    STOP(40),

    /**
     * 出错状态
     *
     */
    ERROR(50),

    /**
     * 完成状态
     */
    COMPLETE(60),

    /**
     * 删除状态
     */
    DELETE(70);

    private int code;

    Status(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static Status codeOf(int code) {
        for (Status status : values()) {
            if (code == status.code()) return status;
        }
        return NEW;
    }
}
