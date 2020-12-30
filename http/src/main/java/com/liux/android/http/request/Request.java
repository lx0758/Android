package com.liux.android.http.request;

import android.text.TextUtils;

import com.liux.android.http.interceptor.TimeoutInterceptor;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by Liux on 2018/2/26.
 */

public abstract class Request<T extends Request> implements okhttp3.Callback {

    private String mUrl;
    private Object mTag;
    private Map<Class<?>, Object> mTags = Collections.emptyMap();
    private Method mMethod;
    private Callback mCallback;
    private WeakReference<RequestManager> mRequestManagerWeakReference;

    private Call mCall;
    private Call.Factory mFactory;

    private IdentityHashMap<String, String> mHeaderHashMap;

    public Request(Call.Factory factory, Method method) {
        mFactory = factory;
        mMethod = method;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Callback callback = getCallback();
        if (callback != null) callback.onFailure(this, e);
        cancel();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) onFailure(call, new HttpException(response));
        response = handlerResponse(response);
        Callback callback = getCallback();
        if (callback != null) callback.onSucceed(this, response);
        cancel();
    }

    public T url(String url) {
        mUrl = url;
        return (T) this;
    }

    public T header(String name, String value) {
        getHeaderHashMap().put(name, value);
        return (T) this;
    }

    public T addHeader(String name, String value) {
        getHeaderHashMap().put(new String(name), value);
        return (T) this;
    }

    public T removeHeader(String name) {
        getHeaderHashMap().remove(name);
        return (T) this;
    }

    public T connectTimeout(int time, TimeUnit timeUnit) {
        long millTime = timeUnit.toMillis(time);
        if (millTime > 0) {
            getHeaderHashMap().put(TimeoutInterceptor.HEADER_TIMEOUT_CONNECT, String.valueOf(millTime));
        } else {
            getHeaderHashMap().remove(TimeoutInterceptor.HEADER_TIMEOUT_CONNECT);
        }
        return (T) this;
    }

    public T writeTimeout(int time, TimeUnit timeUnit) {
        long millTime = timeUnit.toMillis(time);
        if (millTime > 0) {
            getHeaderHashMap().put(TimeoutInterceptor.HEADER_TIMEOUT_WRITE, String.valueOf(millTime));
        } else {
            getHeaderHashMap().remove(TimeoutInterceptor.HEADER_TIMEOUT_WRITE);
        }
        return (T) this;
    }

    public T readTimeout(int time, TimeUnit timeUnit) {
        long millTime = timeUnit.toMillis(time);
        if (millTime > 0) {
            getHeaderHashMap().put(TimeoutInterceptor.HEADER_TIMEOUT_READ, String.valueOf(millTime));
        } else {
            getHeaderHashMap().remove(TimeoutInterceptor.HEADER_TIMEOUT_READ);
        }
        return (T) this;
    }

    public T tag(Object object) {
        mTag = object;
        return (T) this;
    }

    public <TT> T tag(Class<? super TT> type, TT tag) {
        if (mTags.isEmpty()) mTags = new LinkedHashMap<>();
        mTags.put(type, type.cast(tag));
        return (T) this;
    }

    public T manager(RequestManager requestManager) {
        mRequestManagerWeakReference = new WeakReference<>(requestManager);
        return (T) this;
    }

    public Response sync() throws IOException {
        checkUrl();
        cancelCall();
        addManager();
        Response response = handlerCall().execute();
        if (!response.isSuccessful()) throw new HttpException(response);
        response = handlerResponse(response);
        return response;
    }

    public T async() {
        return async(null);
    }

    public T async(Callback callback) {
        checkUrl();
        cancelCall();
        addManager();
        mCallback = callback;
        handlerCall().enqueue(this);
        return (T) this;
    }

    public void cancel() {
        cancelCall();
        removeManager();
        mCallback = null;
    }

    protected abstract HttpUrl.Builder onCreateHttpUrlBuilder(HttpUrl.Builder builder);

    protected abstract HttpUrl onCreateHttpUrl(HttpUrl httpUrl);

    protected abstract okhttp3.Request.Builder onCreateRequestBuilder(okhttp3.Request.Builder builder);

    protected abstract okhttp3.Request onCreateRequest(okhttp3.Request request);

    protected abstract okhttp3.Response.Builder onCreateResponseBuilder(okhttp3.Response.Builder builder);

    protected abstract okhttp3.Response onCreateResponse(okhttp3.Response response);

    protected HttpUrl handlerHttpUrl() {
        HttpUrl.Builder builder = HttpUrl.parse(getUrl()).newBuilder();
        builder = onCreateHttpUrlBuilder(builder);

        HttpUrl httpUrl = builder.build();
        httpUrl = onCreateHttpUrl(httpUrl);

        return httpUrl;
    }

    protected Call handlerCall() {
        HttpUrl httpUrl = handlerHttpUrl();

        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder = onCreateRequestBuilder(builder);

        builder.url(httpUrl).headers(Headers.of(getHeaderHashMap())).tag(getTag()).tag(Request.class, this);
        for (Map.Entry<Class<?>, Object> classObjectEntry : getTags().entrySet()) {
            builder.tag((Class<? super Object>) classObjectEntry.getKey(), classObjectEntry.getValue());
        }

        okhttp3.Request request = builder.build();
        request = onCreateRequest(request);

        mCall = getFactory().newCall(request);
        return mCall;
    }

    protected String getUrl() {
        return mUrl;
    }

    protected Object getTag() {
        return mTag;
    }

    protected Map<Class<?>, Object> getTags() {
        return mTags;
    }

    protected Method getMethod() {
        return mMethod;
    }

    protected Callback getCallback() {
        return mCallback;
    }

    protected RequestManager getCancelManager() {
        if (mRequestManagerWeakReference == null) return null;
        return mRequestManagerWeakReference.get();
    }

    protected Call getCall() {
        return mCall;
    }

    protected Call.Factory getFactory() {
        return mFactory;
    }

    protected IdentityHashMap<String, String> getHeaderHashMap() {
        if (mHeaderHashMap == null) {
            mHeaderHashMap = new IdentityHashMap<>();
        }
        return mHeaderHashMap;
    }

    private void checkUrl() {
        if (TextUtils.isEmpty(getUrl())) {
            throw new NullPointerException("url is empty");
        }
        if (HttpUrl.parse(getUrl()) == null) {
            throw new IllegalArgumentException("\"" + getUrl() + "\" is not right");
        }
    }

    private void cancelCall() {
        Call call = getCall();
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    private void addManager() {
        RequestManager requestManager = getCancelManager();
        if (requestManager != null) {
            requestManager.add(this);
        }
    }

    private void removeManager() {
        RequestManager requestManager = getCancelManager();
        if (requestManager != null) {
            requestManager.remove(this);
        }
    }

    private Response handlerResponse(Response response) {
        Response.Builder builder = onCreateResponseBuilder(response.newBuilder());
        response = onCreateResponse(builder.build());
        return response;
    }
}
