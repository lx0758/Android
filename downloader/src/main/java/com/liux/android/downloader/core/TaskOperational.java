package com.liux.android.downloader.core;

import java.io.File;
import java.io.IOException;

/**
 * 任务操作接口
 */
interface TaskOperational {

    /**
     * 开始自己
     */
    void start();

    /**
     * 停止自己
     */
    void stop();

    /**
     * 重置自己
     */
    void reset();

    /**
     * 删除自己
     */
    void delete();

    /**
     * 同步开始自己
     */
    File syncStart() throws IOException, IllegalStateException;
}
