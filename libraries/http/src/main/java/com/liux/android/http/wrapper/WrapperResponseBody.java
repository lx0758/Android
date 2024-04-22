package com.liux.android.http.wrapper;

import okhttp3.ResponseBody;

/**
 * 2018/4/9
 * By Liux
 * lx0758@qq.com
 */
public interface WrapperResponseBody {

    /**
     * 被包装 ResponseBody 是否是 WrapperResponseBody
     * @return
     */
    boolean isChildWarpper();

    /**
     * 获取被包装的 ResponseBody
     * @return
     */
    ResponseBody getResponseBody();

    /**
     * 设置包装的 ResponseBody
     * @param responseBody
     */
    void setResponseBody(ResponseBody responseBody);
}
