package com.liux.android.http.request;

import com.liux.android.http.progress.OnResponseProgressListener;
import com.liux.android.http.progress.ProgressResponseBody;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * 未处理当 {@link #url(String)} 本身含有参数的情况
 * 目前逻辑是直接添加到参数中,可能会导致参数存在多个
 * Created by Liux on 2018/2/26.
 */

public class QueryRequest<T extends QueryRequest> extends Request<T> {

    private IdentityHashMap<String, String> mQueryHashMap;
    private String mFragment;

    private OnResponseProgressListener mResponseProgressListener;

    public QueryRequest(Call.Factory factory, Method method) {
        super(factory, method);
    }

    @Override
    public T url(String url) {
        return super.url(url);
    }

    @Override
    public T header(String name, String value) {
        return super.header(name, value);
    }

    @Override
    public T addHeader(String name, String value) {
        return super.addHeader(name, value);
    }

    @Override
    public T removeHeader(String name) {
        return super.removeHeader(name);
    }

    @Override
    public T tag(Object object) {
        return super.tag(object);
    }

    @Override
    public T manager(RequestManager requestManager) {
        return super.manager(requestManager);
    }

    @Override
    public T connectTimeout(int time, TimeUnit timeUnit) {
        return super.connectTimeout(time, timeUnit);
    }

    @Override
    public T writeTimeout(int time, TimeUnit timeUnit) {
        return super.writeTimeout(time, timeUnit);
    }

    @Override
    public T readTimeout(int time, TimeUnit timeUnit) {
        return super.readTimeout(time, timeUnit);
    }

    @Override
    protected HttpUrl.Builder onCreateHttpUrlBuilder(HttpUrl.Builder builder) {
        for (Map.Entry<String, String> param : getQueryHashMap().entrySet()) {
            builder.addEncodedQueryParameter(param.getKey(), param.getValue());
        }
        if (mFragment != null) {
            builder.encodedFragment(mFragment);
        }
        return builder;
    }

    @Override
    protected HttpUrl onCreateHttpUrl(HttpUrl httpUrl) {
        return httpUrl;
    }

    @Override
    protected okhttp3.Request.Builder onCreateRequestBuilder(okhttp3.Request.Builder builder) {
        return builder.method(getMethod().toString(), null);
    }

    @Override
    protected okhttp3.Request onCreateRequest(okhttp3.Request request) {
        return request;
    }

    @Override
    protected Response.Builder onCreateResponseBuilder(Response.Builder builder) {
        return builder;
    }

    @Override
    protected Response onCreateResponse(Response response) {
        if (mResponseProgressListener != null) {
            Response.Builder builder = response.newBuilder();
            builder.body(new ProgressResponseBody(response.request().url(), response.body(), mResponseProgressListener));
            response = builder.build();
        }
        return response;
    }

    public T query(String name, String value) {
        getQueryHashMap().put(name, value);
        return (T) this;
    }

    public T addQuery(String name, String value) {
        getQueryHashMap().put(new String(name), value);
        return (T) this;
    }

    public T removeQuery(String name) {
        getQueryHashMap().remove(name);
        return (T) this;
    }

    public T removeQueryAll(String name) {
        for (Iterator<Map.Entry<String, String>> it = getQueryHashMap().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey().equals(name)) {
                it.remove();
            }
        }
        return (T) this;
    }

    public T fragment(String fragment) {
        mFragment = fragment;
        return (T) this;
    }

    public T progress(OnResponseProgressListener listener) {
        mResponseProgressListener = listener;
        return (T) this;
    }

    public T download(File saveFile, DownloadCallback downloadCallback) {
        return async(new DownloadProxy(saveFile, downloadCallback));
    }

   protected IdentityHashMap<String, String> getQueryHashMap() {
        if (mQueryHashMap == null) {
            mQueryHashMap = new IdentityHashMap<>();
        }
        return mQueryHashMap;
    }

    public static class Method extends com.liux.android.http.request.Method {

        public static final Method GET = new Method("GET");
        public static final Method HEAD = new Method("HEAD");

        Method(String method) {
            super(method);
        }
    }
}
