package com.liux.android.http.request;

import android.text.TextUtils;

import com.liux.android.http.interceptor.TimeoutInterceptor;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.FailException;
import java.util.IdentityHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by Liux on 2018/2/26.
 */

public abstract class Request<T extends Request> implements Callback {

    private static final String HEADER_REQUEST_NAME = "Request-From";
    private static final String HEADER_REQUEST_VALUE = "Http";

    public static boolean isManuallyRequest(okhttp3.Request request) {
        String from = request.header(HEADER_REQUEST_NAME);
        return !TextUtils.isEmpty(from) && HEADER_REQUEST_VALUE.equals(from);
    }

    private String mUrl;
    private Object mTag;
    private Method mMethod;
    private Result mResult;
    private WeakReference<RequestManager> mRequestManagerWeakReference;

    private Call mCall;
    private Call.Factory mFactory;

    private IdentityHashMap<String, String> mHeaderHashMap;

    public Request(Call.Factory factory, Method method) {
        mFactory = factory;
        mMethod = method;

        distinguishRequest(true);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Result result = getResult();
        if (result != null) result.onFailure(call, e);
        cancel();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) onFailure(call, new FailException(response));
        response = handlerResponse(response);
        Result result = getResult();
        if (result != null) result.onSucceed(call, response);
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

    public T distinguishRequest(boolean distinguish) {
        if (distinguish) {
            getHeaderHashMap().put(HEADER_REQUEST_NAME, HEADER_REQUEST_VALUE);
        } else {
            getHeaderHashMap().remove(HEADER_REQUEST_NAME);
        }
        return (T) this;
    }

    public T tag(Object object) {
        mTag = object;
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
        if (!response.isSuccessful()) throw new FailException(response);
        response = handlerResponse(response);
        cancel();
        return response;
    }

    public void async() {
        async(null);
    }

    public void async(Result result) {
        checkUrl();
        cancelCall();
        addManager();
        mResult = result;
        handlerCall().enqueue(this);
    }

    public void cancel() {
        cancelCall();
        removeManager();
        mResult = null;
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

        builder.url(httpUrl).tag(getTag()).headers(Headers.of(getHeaderHashMap()));

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

    protected Method getMethod() {
        return mMethod;
    }

    protected Result getResult() {
        return mResult;
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
        if (call != null && !call.isCanceled() && !call.isExecuted()) {
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
