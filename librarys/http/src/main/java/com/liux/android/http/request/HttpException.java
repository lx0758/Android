package com.liux.android.http.request;

import java.io.IOException;

import okhttp3.Response;

/**
 * 2018/4/11
 * By Liux
 * lx0758@qq.com
 */
public class HttpException extends IOException {

    private Response mResponse;

    public HttpException() {
    }

    public HttpException(String msg) {
        super(msg);
    }

    public HttpException(Response response) {
        super(response.message());
        mResponse = response;
    }

    public Response getResponse() {
        return mResponse;
    }

    @Override
    public String toString() {
        String s = getClass().getSimpleName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
