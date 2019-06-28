package com.liux.android.http.request;

import com.liux.android.http.HttpUtil;
import com.liux.android.http.progress.OnProgressListener;
import com.liux.android.http.progress.OnRequestProgressListener;
import com.liux.android.http.progress.OnResponseProgressListener;
import com.liux.android.http.progress.ProgressRequestBody;

import java.io.File;
import java.io.InputStream;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.*;
import okhttp3.Request;
import okio.ByteString;

/**
 * Created by Liux on 2018/2/26.
 */

public class BodyRequest<T extends BodyRequest> extends QueryRequest<T> {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FORM = 1;

    private int mType = TYPE_NORMAL;

    private String mBodyType;
    private Object mBodyObject;

    private boolean mIsMultipart = false;
    private IdentityHashMap<String, Object> mBodyHashMap;

    private OnRequestProgressListener mOnRequestProgressListener;

    public BodyRequest(Call.Factory factory, Method method) {
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
    public T distinguishRequest(boolean distinguish) {
        return super.distinguishRequest(distinguish);
    }

    @Override
    public T fragment(String fragment) {
        return super.fragment(fragment);
    }

    @Override
    public T progress(OnResponseProgressListener listener) {
        return super.progress(listener);
    }

    @Override
    public T tag(Object object) {
        return super.tag(object);
    }

    @Override
    protected Request.Builder onCreateRequestBuilder(Request.Builder builder) {
        return builder.method(getMethod().toString(), getRequestBody());
    }

    @Override
    public T manager(RequestManager requestManager) {
        return super.manager(requestManager);
    }

    @Override
    protected Request onCreateRequest(Request request) {
        if (mOnRequestProgressListener != null) {
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                requestBody = new ProgressRequestBody(request.url(), request.body(), mOnRequestProgressListener);
            }
            request = request.newBuilder()
                    .method(request.method(), requestBody)
                    .build();
        }
        return request;
    }

    @Override
    public T query(String name, String value) {
        return super.query(name, value);
    }

    @Override
    public T addQuery(String name, String value) {
        return super.addQuery(name, value);
    }

    @Override
    public T removeQuery(String name) {
        return super.removeQuery(name);
    }

    @Override
    public T removeQueryAll(String name) {
        return super.removeQueryAll(name);
    }

    public T body(String type, String string) {
        bodyObject(type, string);
        return (T) this;
    }

    public T body(String type, ByteString byteString) {
        bodyObject(type, byteString);
        return (T) this;
    }

    public T body(String type, byte[] bytes) {
        bodyObject(type, bytes);
        return (T) this;
    }

    public T body(String type, InputStream inputStream) {
        bodyObject(type, inputStream);
        return (T) this;
    }

    public T body(String type, File file) {
        bodyObject(type, file);
        return (T) this;
    }

    public T body(RequestBody requestBody) {
        bodyObject(null, requestBody);
        return (T) this;
    }

    public T form() {
        mIsMultipart = false;
        return (T) this;
    }

    public T multipart() {
        mIsMultipart = true;
        return (T) this;
    }

    public T param(String name, String string) {
        paramObject(name, string);
        return (T) this;
    }

    public T param(String name, byte[] bytes) {
        param(name, null, bytes);
        return (T) this;
    }

    public T param(String name, String type, byte[] bytes) {
        multipart();
        paramObject(name, HttpUtil.parseBytePart(name, type, bytes));
        return (T) this;
    }

    public T param(String name, InputStream inputStream) {
        param(name, null, inputStream);
        return (T) this;
    }

    public T param(String name, String type, InputStream inputStream) {
        multipart();
        paramObject(name, HttpUtil.parseInputStreamPart(name, type, inputStream));
        return (T) this;
    }

    public T param(String name, File file) {
        return param(name, file.getName(), file);
    }

    public T param(String name, String fileName, File file) {
        multipart();
        paramObject(name, HttpUtil.parseFilePart(name, fileName, file));
        return (T) this;
    }

    public T param(String name, MultipartBody.Part part) {
        multipart();
        paramObject(name, part);
        return (T) this;
    }

    public T addParam(String name, String string) {
        addParamObject(name, string);
        return (T) this;
    }

    public T addParam(String name, byte[] bytes) {
        addParam(name, null, bytes);
        return (T) this;
    }

    public T addParam(String name, String type, byte[] bytes) {
        multipart();
        addParamObject(name, HttpUtil.parseBytePart(name, type, bytes));
        return (T) this;
    }

    public T addParam(String name, InputStream inputStream) {
        addParam(name, null, inputStream);
        return (T) this;
    }

    public T addParam(String name, String type, InputStream inputStream) {
        multipart();
        addParamObject(name, HttpUtil.parseInputStreamPart(name, type, inputStream));
        return (T) this;
    }

    public T addParam(String name, File file) {
        return addParam(name, file.getName(), file);
    }

    public T addParam(String name, String fileName, File file) {
        multipart();
        addParamObject(name, HttpUtil.parseFilePart(name, fileName, file));
        return (T) this;
    }

    public T addParam(String name, MultipartBody.Part part) {
        multipart();
        addParamObject(name, part);
        return (T) this;
    }

    public T removeParam(String name) {
        getBodyHashMap().remove(name);
        return (T) this;
    }

    public T removeParamAll(String name) {
        for (Iterator<Map.Entry<String, Object>> it = getBodyHashMap().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> entry = it.next();
            if (entry.getKey().equals(name)) {
                it.remove();
            }
        }
        return (T) this;
    }

    public T progress(OnProgressListener listener) {
        progress((OnRequestProgressListener) listener);
        progress((OnResponseProgressListener) listener);
        return (T) this;
    }

    public T progress(OnRequestProgressListener listener) {
        mOnRequestProgressListener = listener;
        return (T) this;
    }

    protected IdentityHashMap<String, Object> getBodyHashMap() {
        if (mBodyHashMap == null) {
            mBodyHashMap = new IdentityHashMap<>();
        }
        return mBodyHashMap;
    }

    private void bodyObject(String type, Object object) {
        mType = TYPE_NORMAL;
        mBodyType = type;
        mBodyObject = object;
    }

    private void paramObject(String name, Object object) {
        mType = TYPE_FORM;
        getBodyHashMap().put(name, object);
    }

    private void addParamObject(String name, Object object) {
        mType = TYPE_FORM;
        getBodyHashMap().put(new String(name), object);
    }

    private RequestBody getRequestBody() {
        RequestBody requestBody;
        switch (mType) {
            case TYPE_NORMAL:
                requestBody = onCreateRequestBody();
                break;
            case TYPE_FORM:
                if (!mIsMultipart) {
                    requestBody = onCreateFormBody();
                } else {
                    requestBody = onCreateMultipartBody();
                }
                break;
            default:
                requestBody = null;
                break;
        }
        return requestBody;
    }

    private RequestBody onCreateRequestBody() {
        if (mBodyObject instanceof String) {
            return RequestBody.create(MediaType.parse(mBodyType), (String) mBodyObject);
        } else if (mBodyObject instanceof ByteString) {
            return RequestBody.create(MediaType.parse(mBodyType), (ByteString) mBodyObject);
        } else if (mBodyObject instanceof byte[]) {
            return RequestBody.create(MediaType.parse(mBodyType), (byte[]) mBodyObject);
        } else if (mBodyObject instanceof InputStream) {
            return HttpUtil.parseInputStream(mBodyType, (InputStream) mBodyObject);
        } else if (mBodyObject instanceof File) {
            return RequestBody.create(MediaType.parse(mBodyType), (File) mBodyObject);
        } else if ((mBodyObject instanceof RequestBody)) {
            return (RequestBody) mBodyObject;
        }
        return null;
    }

    private RequestBody onCreateFormBody() {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : getBodyHashMap().entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (name != null && value != null) {
                if (value instanceof String) {
                    builder.addEncoded(name, (String) value);
                } else {
                    builder.addEncoded(name, value.toString());
                }
            }
        }
        return builder.build();
    }

    private RequestBody onCreateMultipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> entry : getBodyHashMap().entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (name != null && value != null) {
                if (value instanceof String){
                    builder.addFormDataPart(name, null, HttpUtil.parseString((String) value));
                } else if (value instanceof MultipartBody.Part) {
                    builder.addPart((MultipartBody.Part) value);
                } else {
                    builder.addFormDataPart(name, null, HttpUtil.parseString(value.toString()));
                }
            }
        }
        return builder.build();
    }

    public static class Method extends QueryRequest.Method {

        public static final Method POST = new Method("POST");
        public static final Method PUT = new Method("PUT");
        public static final Method PATCH = new Method("PATCH");
        public static final Method DELETE = new Method("DELETE");

        Method(String method) {
            super(method);
        }
    }
}
