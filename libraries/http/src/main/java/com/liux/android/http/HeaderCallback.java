package com.liux.android.http;

import java.util.Map;

import okhttp3.Request;

/**
 * 请求头部监听器
 * Created by Liux on 2017/11/29.
 */

public interface HeaderCallback {

    /**
     * Header 回调, 支持一个 key 对应多个 value <br>
     * headers.put(key, value); 覆盖
     * headers.put(new String(key), value); 增加
     * @param request
     * @param headers
     */
    void onHeaders(Request request, Map<String, String> headers);
}
