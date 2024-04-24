package com.liux.android.http.interceptor;

import androidx.annotation.NonNull;

import com.liux.android.http.HttpUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Liux on 2017/11/29.
 */

public class UserAgentInterceptor implements Interceptor {
    private String mUserAgent = System.getProperty("http.agent");

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.header("User-Agent", mUserAgent);
        return chain.proceed(builder.build());
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public void setUserAgent(String userAgent) {
        this.mUserAgent = HttpUtil.checkHeaderChar(userAgent);
    }
}
