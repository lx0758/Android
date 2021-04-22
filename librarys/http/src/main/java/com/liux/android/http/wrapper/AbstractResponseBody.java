package com.liux.android.http.wrapper;

import okhttp3.ResponseBody;

/**
 * 2018/4/11
 * By Liux
 * lx0758@qq.com
 */
public abstract class AbstractResponseBody extends ResponseBody implements WrapperResponseBody {

    private ResponseBody mResponseBody;

    public AbstractResponseBody(ResponseBody responseBody) {
        mResponseBody = responseBody;
    }

    @Override
    public boolean isChildWarpper() {
        return mResponseBody instanceof WrapperResponseBody;
    }

    @Override
    public ResponseBody getResponseBody() {
        if (isChildWarpper()) {
            return ((WrapperResponseBody) mResponseBody).getResponseBody();
        }
        return mResponseBody;
    }

    @Override
    public void setResponseBody(ResponseBody responseBody) {
        if (isChildWarpper()) {
            ((WrapperResponseBody) mResponseBody).setResponseBody(responseBody);
            return;
        }
        mResponseBody = responseBody;
    }
}
