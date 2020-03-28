package com.liux.android.http.request;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 2018/4/10
 * By Liux
 * lx0758@qq.com
 */
public interface Callback {

    void onSucceed(Request request, Response response) throws IOException;

    void onFailure(Request request, IOException e);
}
