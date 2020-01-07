package com.liux.android.http;

import java.util.Map;

import okhttp3.Request;

/**
 * 请求动作监听器
 * Created by Liux on 2017/11/29.
 */

public interface OnRequestListener {

    /**
     * 不带请求体的请求回调,在此处修改键值 <br>
     * 支持一个 key 对应多个 value <br>
     * queryParams.put(key, value); 覆盖
     * queryParams.put(new String(key), value); 增加
     * @param request
     * @param queryParams
     */
    void onQueryRequest(Request request, Map<String, String> queryParams);

    /**
     * 带请求体的请求回调,在此处修改参数 <br>
     * 支持一个 key 对应多个 value <br>
     * queryParams.put(key, value); 覆盖
     * queryParams.put(new String(key), value); 增加
     * bodyParams.put(key, value); 覆盖
     * bodyParams.put(new String(key), value); 增加
     * @param request
     * @param queryParams
     * @param bodyParams
     */
    void onBodyRequest(Request request, Map<String, String> queryParams, Map<String, String> bodyParams);

    /**
     * 纯文本/字节的请求回调,在此处修改数据
     * 支持一个 key 对应多个 value <br>
     * queryParams.put(key, value); 覆盖
     * queryParams.put(new String(key), value); 增加
     * @param request
     * @param queryParams
     * @param bodyParam
     */
    void onBodyRequest(Request request, Map<String, String> queryParams, BodyParam bodyParam);

    class BodyParam {

        private String type;
        private String string;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}
