package com.liux.android.http.wrapper;

import okhttp3.RequestBody;

/**
 * 2018/4/9
 * By Liux
 * lx0758@qq.com
 */
public interface WrapperRequestBody {

    /**
     * 被包装 RequestBody 是否是 WrapperRequestBody
     * @return
     */
    boolean isChildWarpper();

    /**
     * 被包装 RequestBody 是否是 FormBody
     * @return
     */
    boolean isFormBody();

    /**
     * 被包装 RequestBody 是否是 MultipartBody
     * @return
     */
    boolean isMultipartBody();

    /**
     * 获取被包装的 RequestBody
     * @return
     */
    RequestBody getRequestBody();

    /**
     * 设置包装的 RequestBody
     * @param requestBody
     */
    void setRequestBody(RequestBody requestBody);
}
