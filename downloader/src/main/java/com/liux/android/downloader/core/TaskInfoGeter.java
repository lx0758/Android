package com.liux.android.downloader.core;

import com.liux.android.downloader.Status;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务信息获取接口
 */
interface TaskInfoGeter {

    /**
     * 获取任务ID
     * @return
     */
    long getId();

    /**
     * 获取任务链接
     * @return
     */
    String getUrl();

    /**
     * 获取任务请求方法
     * @return
     */
    String getMethod();

    /**
     * 获取任务请求头
     * @return
     */
    Map<String, List<String>> getHeaders();

    /**
     * 获取任务存储文件
     * @return
     */
    File getFile();

    /**
     * 获取任务已完成量
     * @return
     */
    long getCompleted();

    /**
     * 获取任务总大小
     * @return
     */
    long getTotal();

    /**
     * 获取任务下载速度(byte/s)
     * @return
     */
    long getSpeed();

    /**
     * 取任务状态
     * @return
     */
    Status getStatus();

    /**
     * 获取任务创建时间
     * @return
     */
    Date getCreateTime();

    /**
     * 获取任务最后更新时间
     * @return
     */
    Date getUpdateTime();

    /**
     * 获取出错时的错误信息
     * @return
     */
    Throwable getErrorInfo();
}
