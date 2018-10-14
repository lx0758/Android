package com.liux.android.http.request;

/**
 * 2018/2/27
 * By Liux
 * lx0758@qq.com
 */

public class Method {

    private String mMethod;

    Method(String method) {
        mMethod = method;
    }

    @Override
    public String toString() {
        return mMethod;
    }
}
