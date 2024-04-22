package com.liux.android.http.interceptor;

import com.liux.android.http.HeaderCallback;
import com.liux.android.http.HttpUtil;
import com.liux.android.http.RequestCallback;
import com.liux.android.http.wrapper.WrapperRequestBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 请求头/请求参数拦截器
 * Created by Liux on 2017/11/29.
 */

public class RequestInterceptor implements Interceptor {

    private HeaderCallback mHeaderCallback;
    private RequestCallback mRequestCallback;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder();

        /* 请求定制：自定义请求头 */
        if (mHeaderCallback != null) {
            try {
                checkHeader(request, requestBuilder);
            } catch (Exception e) {
                requestBuilder.headers(request.headers());
            }
        }

        /* 请求体定制：自定义参数(针对文本型) */
        if (mRequestCallback != null) {
            try {
                String method = request.method();
                if (HttpUtil.notRequiresRequestBody(method)) {
                    // 不允许有请求体的
                    checkQueryRequest(request, requestBuilder);
                } else {
                    // 允许有请求体的
                    checkBodyRequest(request, requestBuilder);
                }
            } catch (Exception e) {
                requestBuilder.url(request.url()).method(request.method(), request.body());
            }
        }

        request = requestBuilder.build();

        return chain.proceed(request);
    }

    public void setHeaderCallback(HeaderCallback listener) {
        mHeaderCallback = listener;
    }

    public void setRequestCallback(RequestCallback listener) {
        mRequestCallback = listener;
    }

    /**
     * 检查请求头
     * @param request
     * @param requestBuilder
     */
    private void checkHeader(Request request, Request.Builder requestBuilder) {
        Map<String, String> headers = new IdentityHashMap<>();

        // 取出数据
        Headers oldHeaders = request.headers();
        for (String key : oldHeaders.names()) {
            for (String value : oldHeaders.values(key)) {
                headers.put(new String(key), value);
            }
        }

        mHeaderCallback.onHeaders(request, headers);

        // 合成新的 Header
        Headers okHeaders = Headers.of(headers);

        requestBuilder.headers(okHeaders);
    }

    /**
     * 逆向解析 GET/HEAD 请求并检查参数
     * @param request
     * @return
     */
    private void checkQueryRequest(Request request, Request.Builder requestBuilder) {
        Map<String, String> queryParams = new IdentityHashMap<>();
        resolveQueryParam(request, queryParams);

        mRequestCallback.onQueryRequest(request, queryParams);

        HttpUrl httpUrl = revertQueryParam(request, queryParams);

        requestBuilder.url(httpUrl).method(request.method(), null);
    }

    /**
     * 逆向解析 POST/DELETE/PUT/PATCH 请求并检查参数(目前只处理文本型参数)
     * @param request
     * @param requestBuilder
     */
    private void checkBodyRequest(Request request, Request.Builder requestBuilder) throws IOException {
        RequestBody requestBody = request.body();
        if (requestBody == null) return;
        if (requestBody instanceof WrapperRequestBody) {
            WrapperRequestBody wrapperRequestBody = (WrapperRequestBody) requestBody;
            checkWrapperRequestBodyParams(request, wrapperRequestBody, requestBuilder);
        } else if (requestBody instanceof MultipartBody) {
            checkMultipartBodyParams(request, (MultipartBody) requestBody, requestBuilder);
        } else if (requestBody instanceof FormBody) {
            checkFormBodyParams(request, (FormBody) requestBody, requestBuilder);
        } else {
            checkRequestBodyParams(request, requestBody, requestBuilder);
        }
    }

    private void checkWrapperRequestBodyParams(Request request, WrapperRequestBody wrapperRequestBody, Request.Builder requestBuilder) throws IOException {
        Map<String, String> queryParams = new IdentityHashMap<>();
        resolveQueryParam(request, queryParams);

        RequestBody requestBody = wrapperRequestBody.getRequestBody();
        Map<String, String> bodyParams = new IdentityHashMap<>();

        MultipartBody multipartBody = null;
        Map<String, String> bodyTypes = null;
        List<MultipartBody.Part> oldParts = null;

        FormBody formBody = null;

        if (wrapperRequestBody.isMultipartBody()) {
            multipartBody = (MultipartBody) requestBody;
            bodyTypes = new IdentityHashMap<>();
            oldParts = new ArrayList<>();
            // 读取原始参数
            for (MultipartBody.Part part : multipartBody.parts()) {
                Headers head  = part.headers();
                RequestBody body  = part.body();
                if (HttpUtil.isTextMediaType(body.contentType())) {
                    Buffer buffer = new Buffer();
                    body.writeTo(buffer);
                    String key = head.value(0).substring(head.value(0).lastIndexOf("=") + 1).replace("\"", "");
                    String value = buffer.readUtf8();
                    String rel_key = new String(key);
                    bodyParams.put(rel_key, value);
                    bodyTypes.put(rel_key, body.contentType().toString());
                } else {
                    oldParts.add(part);
                }
            }
        } else if (wrapperRequestBody.isFormBody()) {
            formBody = (FormBody) requestBody;
            // 读取原始参数
            for (int i = 0; i < formBody.size(); i++) {
                bodyParams.put(new String(formBody.name(i)), formBody.value(i));
            }
        }

        mRequestCallback.onBodyRequest(request, queryParams, bodyParams);

        if (wrapperRequestBody.isMultipartBody()) {
            // 恢复参数并组合成新的 MultipartBody
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            bodyBuilder.setType(multipartBody.type());
            for (MultipartBody.Part part : oldParts) {
                bodyBuilder.addPart(part);
            }
            for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
                bodyBuilder.addPart(HttpUtil.parseStringPart(entry.getKey(), bodyTypes.get(entry.getKey()), entry.getValue()));
            }
            multipartBody = bodyBuilder.build();
            requestBody = multipartBody;
        } else if (wrapperRequestBody.isFormBody()) {
            // 组合成新的  FormBody
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
                bodyBuilder.add(entry.getKey(), entry.getValue());
            }
            formBody = bodyBuilder.build();
            requestBody = formBody;
        }

        wrapperRequestBody.setRequestBody(requestBody);

        HttpUrl httpUrl = revertQueryParam(request, queryParams);

        requestBuilder.url(httpUrl).method(request.method(), (RequestBody) wrapperRequestBody);
    }

    private void checkMultipartBodyParams(Request request, MultipartBody multipartBody, Request.Builder requestBuilder) throws IOException {
        Map<String, String> queryParams = new IdentityHashMap<>();
        resolveQueryParam(request, queryParams);

        Map<String, String> bodyParams = new IdentityHashMap<>();
        Map<String, String> bodyTypes = new IdentityHashMap<>();
        List<MultipartBody.Part> oldParts = new ArrayList<>();
        // 读取原始参数
        for (MultipartBody.Part part : multipartBody.parts()) {
            Headers head  = part.headers();
            RequestBody body  = part.body();
            if (HttpUtil.isTextMediaType(body.contentType())) {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                String key = head.value(0).substring(head.value(0).lastIndexOf("=") + 1).replace("\"", "");
                String value = buffer.readUtf8();
                String rel_key = new String(key);
                bodyParams.put(rel_key, value);
                bodyTypes.put(rel_key, body.contentType().toString());
            } else {
                oldParts.add(part);
            }
        }

        mRequestCallback.onBodyRequest(request, queryParams, bodyParams);

        // 恢复参数并组合成新的 MultipartBody
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(multipartBody.type());
        for (MultipartBody.Part part : oldParts) {
            bodyBuilder.addPart(part);
        }
        for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
            bodyBuilder.addPart(HttpUtil.parseStringPart(entry.getKey(), bodyTypes.get(entry.getKey()), entry.getValue()));
        }
        multipartBody = bodyBuilder.build();

        HttpUrl httpUrl = revertQueryParam(request, queryParams);

        requestBuilder.url(httpUrl).method(request.method(), multipartBody);
    }

    private void checkFormBodyParams(Request request, FormBody formBody, Request.Builder requestBuilder) throws IOException {
        Map<String, String> queryParams = new IdentityHashMap<>();
        resolveQueryParam(request, queryParams);

        Map<String, String> bodyParams = new IdentityHashMap<>();
        // 读取原始参数
        for (int i = 0; i < formBody.size(); i++) {
            bodyParams.put(new String(formBody.name(i)), formBody.value(i));
        }

        mRequestCallback.onBodyRequest(request, queryParams, bodyParams);

        // 组合成新的  FormBody
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
            bodyBuilder.add(entry.getKey(), entry.getValue());
        }
        formBody = bodyBuilder.build();

        HttpUrl httpUrl = revertQueryParam(request, queryParams);

        requestBuilder.url(httpUrl).method(request.method(), formBody);
    }

    private void checkRequestBodyParams(Request request, RequestBody requestBody, Request.Builder requestBuilder) throws IOException {
        Map<String, String> queryParams = new IdentityHashMap<>();
        resolveQueryParam(request, queryParams);

        RequestCallback.BodyParam bodyParam = null;

        MediaType mediaType = requestBody.contentType();
        if (HttpUtil.isTextMediaType(mediaType)) {
            bodyParam = new RequestCallback.BodyParam();
            bodyParam.setType(mediaType.toString());
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            bodyParam.setString(buffer.readUtf8());
        }

        mRequestCallback.onBodyRequest(request, queryParams, bodyParam);

        if (bodyParam != null) {
            String typeString = bodyParam.getType();
            if (typeString == null) typeString = "";
            String bodyString = bodyParam.getString();
            if (bodyString == null) bodyString = "";
            requestBody = RequestBody.create(MediaType.parse(typeString), bodyString);
        }

        HttpUrl httpUrl = revertQueryParam(request, queryParams);

        requestBuilder.url(httpUrl).method(request.method(), requestBody);
    }

    /**
     * 解析参数
     * @param request
     * @param queryParams
     */
    private void resolveQueryParam(Request request, Map<String, String> queryParams) {
        // 获取原始参数
        Set<String> parameterNames = request.url().queryParameterNames();
        for (String key : parameterNames) {
            queryParams.put(new String(key), request.url().queryParameter(key));
        }
    }

    /**
     * 恢复参数并组合成新的 HttpUrl
     * @param request
     * @param queryParams
     * @return
     */
    private HttpUrl revertQueryParam(Request request, Map<String, String> queryParams) {
        HttpUrl url = request.url();
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme(url.scheme())
                .username(url.username())
                .password(url.password())
                .host(url.host())
                .port(url.port());
        for (String path : url.pathSegments()) {
            urlBuilder.addPathSegment(path);
        }
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            urlBuilder.addQueryParameter(param.getKey(), param.getValue());
        }
        urlBuilder.encodedFragment(url.fragment());
        return urlBuilder.build();
    }
}
