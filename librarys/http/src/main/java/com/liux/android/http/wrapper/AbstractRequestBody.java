package com.liux.android.http.wrapper;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 2018/4/11
 * By Liux
 * lx0758@qq.com
 */
public abstract class AbstractRequestBody extends RequestBody implements WrapperRequestBody {

    private RequestBody mRequestBody;

    public AbstractRequestBody(RequestBody requestBody) {
        mRequestBody = requestBody;
    }

    @Override
    public boolean isChildWarpper() {
        return mRequestBody instanceof WrapperRequestBody;
    }

    @Override
    public boolean isFormBody() {
        if (isChildWarpper()) {
            return ((WrapperRequestBody) mRequestBody).isFormBody();
        }
        return mRequestBody instanceof FormBody;
    }

    @Override
    public boolean isMultipartBody() {
        if (isChildWarpper()) {
            return ((WrapperRequestBody) mRequestBody).isMultipartBody();
        }
        return mRequestBody instanceof MultipartBody;
    }

    @Override
    public RequestBody getRequestBody() {
        if (isChildWarpper()) {
            return ((WrapperRequestBody) mRequestBody).getRequestBody();
        }
        return mRequestBody;
    }

    @Override
    public void setRequestBody(RequestBody requestBody) {
        if (isChildWarpper()) {
            ((WrapperRequestBody) mRequestBody).setRequestBody(requestBody);
            return;
        }
        mRequestBody = requestBody;
    }
}
