package com.liux.android.http;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.liux.android.http.request.Request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import libcore.net.MimeUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.CallAdapter;

/**
 * Http 协议配套工具类
 * Created by Liux on 2017/9/3.
 */

public class HttpUtil {
    public static final MediaType TYPE_UNKNOWN = MediaType.parse("*/*");
    public static final MediaType TYPE_TEXT = MediaType.parse("text/plain;charset=UTF-8");
    public static final MediaType TYPE_JSON = MediaType.parse("application/json;charset=UTF-8");
    public static final MediaType TYPE_XML = MediaType.parse("text/xml;charset=UTF-8");

    /**
     * 查询某字符串是否是HTTP请求方法(支持HTTP/1.1)
     * @param method
     * @return
     */
    public static boolean isHttpMethod(String method) {
        if (method == null || method.isEmpty()) return false;
        switch (method.toUpperCase()) {
            // HTTP/0.9
            case "GET":
            // HTTP/1.0
            case "HEAD":
            case "POST":
            // HTTP/1.1
            case "PUT":
            case "CONNECT":
            case "TRACE":
            case "OPTIONS":
            case "DELETE":
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否是通过 Http 手动创建的请求
     * @param request
     * @return
     */
    public static boolean isManuallyRequest(okhttp3.Request request) {
        return request.tag(Request.class) != null;
    }

    /**
     * 不需要请求体的方法
     * @param method
     * @return
     */
    public static boolean notRequiresRequestBody(String method) {
        if (!isHttpMethod(method)) return false;
        method = method.toUpperCase();
        return !HttpMethod.permitsRequestBody(method)
                || method.equals("GET")
                || method.equals("HEAD")
                || method.equals("TRACE")
                || method.equals("CONNECT");
    }

    /**
     * 需要请求体的方法(DELETE可以为空)
     * @param method
     * @return
     */
    public static boolean requiresRequestBody(String method) {
        if (!isHttpMethod(method)) return false;
        method = method.toUpperCase();
        return HttpMethod.requiresRequestBody(method);
    }

    /**
     * 允许有请求体的方法
     * @param method
     * @return
     */
    public static boolean permitsRequestBody(String method) {
        if (!isHttpMethod(method)) return false;
        method = method.toUpperCase();
        return HttpMethod.permitsRequestBody(method);
    }

    /**
     * 根据文件后缀名解析类型
     * @param file
     * @return
     */
    public static MediaType getMimeType(File file) {
        if (file == null) return TYPE_UNKNOWN;
        return getMimeType(file.getName());
    }

    /**
     * 根据文件后缀名解析类型
     * @param filename
     * @return
     */
    public static MediaType getMimeType(String filename) {
        if (filename == null) return TYPE_UNKNOWN;

        String[] ss = filename.split("\\.");
        if (ss.length < 2) return TYPE_UNKNOWN;

        String suffix = ss[ss.length - 1];
        String type = MimeUtils.guessMimeTypeFromExtension(suffix);

        if (type == null) return TYPE_UNKNOWN;
        return MediaType.parse(type);
    }

    /**
     * 根据媒体类型解析后缀
     * @param type
     * @return
     */
    public static String getMimeSuffix(MediaType type) {
        if (type == null) return "";

        return getMimeSuffix(type.toString());
    }

    /**
     * 根据媒体类型解析后缀
     * @param type
     * @return
     */
    public static String getMimeSuffix(String type) {
        if (type == null) return "";

        String suffix = MimeUtils.guessExtensionFromMimeType(type);

        if (suffix != null) return suffix;
        return "";
    }

    /**
     * 是否是文本型媒体
     * @param type
     * @return
     */
    public static boolean isTextMediaType(MediaType type) {
        if (type == null) return false;
        String t1 = type.type().toLowerCase();
        String t2 = type.subtype().toLowerCase();
        String t = t1 + t2;
        return "text".equals(t1)
                || (TYPE_JSON.type() + TYPE_JSON.subtype()).equals(t);
    }

    /**
     * 生成一个XML请求体
     * @param content
     * @return
     */
    public static RequestBody parseXmlBody(String content) {
        return parseStringBody(TYPE_XML.toString(), content);
    }

    /**
     * 生成一个JSON请求体
     * @param content
     * @return
     */
    public static RequestBody parseJsonBody(String content) {
        return parseStringBody(TYPE_JSON.toString(), content);
    }

    /**
     * 生成一个指定类型请求体
     * @param content
     * @return
     */
    public static RequestBody parseStringBody(String content) {
        return parseStringBody(TYPE_TEXT.toString(), content);
    }

    /**
     * 生成一个指定类型请求体
     * @param content
     * @return
     */
    public static RequestBody parseStringBody(String type, String content) {
        MediaType mediaType = null;
        if (!TextUtils.isEmpty(type)) {
            mediaType = MediaType.parse(type);
        }
        return RequestBody.create(mediaType, content);
    }

    /**
     * 生成一个指定类型请求体
     * @param bytes
     * @return
     */
    public static RequestBody parseByteBody(String type, byte[] bytes) {
        MediaType mediaType = null;
        if (!TextUtils.isEmpty(type)) {
            mediaType = MediaType.parse(type);
        }
        return RequestBody.create(mediaType, bytes);
    }

    /**
     * 生成一个指定类型请求体
     * @param inputStream
     * @return
     */
    public static RequestBody parseInputStreamBody(String type, InputStream inputStream) {
        MediaType mediaType = null;
        if (!TextUtils.isEmpty(type)) {
            mediaType = MediaType.parse(type);
        }

        if (inputStream == null) throw new NullPointerException("inputStream == null");
        if (!inputStream.markSupported()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            copyStream(inputStream, byteArrayOutputStream);
            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }

        final MediaType finalMediaType = mediaType;
        final InputStream finalInputStream = inputStream;
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return finalMediaType;
            }

            @Override
            public long contentLength() throws IOException {
                return finalInputStream.available();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                if (finalInputStream.markSupported()) finalInputStream.reset();
                sink.writeAll(Okio.source(finalInputStream));
            }
        };
    }

    /**
     * 生成一个指定类型请求体
     * @param uri
     * @return
     */
    public static RequestBody parseUriBody(String type, final Uri uri) {
        MediaType mediaType = null;
        if (!TextUtils.isEmpty(type)) {
            mediaType = MediaType.parse(type);
        }

        final MediaType finalMediaType = mediaType;
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return finalMediaType;
            }

            @Override
            public long contentLength() throws IOException {
                return getContentLength();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeAll(Okio.source(getInputStream()));
            }

            private Long contentLength;
            private long getContentLength() throws IOException {
                if (contentLength == null) {
                    InputStream inputStream = getInputStream();
                    contentLength = (long) inputStream.available();
                    inputStream.close();
                }
                return contentLength;
            }

            private InputStream getInputStream() throws IOException {
                return Http.get().getContext().getContentResolver().openInputStream(uri);
            }
        };
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param type
     * @param string
     * @return
     */
    public static MultipartBody.Part parseStringPart(String name, String type, String string) {
        return MultipartBody.Part.createFormData(name, null, parseStringBody(type, string));
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param type
     * @param bytes
     * @return
     */
    public static MultipartBody.Part parseBytePart(String name, String type, byte[] bytes) {
        return parseBytePart(name, null, type, bytes);
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param type
     * @param bytes
     * @return
     */
    public static MultipartBody.Part parseBytePart(String name, String filename, String type, byte[] bytes) {
        return MultipartBody.Part.createFormData(name, filename, parseByteBody(type, bytes));
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param type
     * @param inputStream
     * @return
     */
    public static MultipartBody.Part parseInputStreamPart(String name, String type, InputStream inputStream) {
        return parseInputStreamPart(name, null, type, inputStream);
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param filename
     * @param type
     * @param inputStream
     * @return
     */
    public static MultipartBody.Part parseInputStreamPart(String name, String filename, String type, InputStream inputStream) {
        return MultipartBody.Part.createFormData(name, filename, parseInputStreamBody(type, inputStream));
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param file
     * @return
     */
    public static MultipartBody.Part parseFilePart(String name, File file) {
        return parseFilePart(name, file.getName(), file);
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param file
     * @param filename
     * @return
     */
    public static MultipartBody.Part parseFilePart(String name, String filename, File file) {
        MediaType mediaType = getMimeType(file);
        return MultipartBody.Part.createFormData(name, filename, RequestBody.create(mediaType, file));
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param uri
     * @return
     */
    public static MultipartBody.Part parseUriPart(String name, Uri uri) {
        String filename = null;
        String type = null;

        String scheme = uri.getScheme();
        switch (scheme) {
            case ContentResolver.SCHEME_CONTENT:
                Cursor cursor = null;
                try {
                    cursor = Http.get().getContext().getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.MIME_TYPE}, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int filenameColumns = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                        int typeColumns = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
                        filename = filenameColumns != -1 ? cursor.getString(filenameColumns) : "unknown";
                        type = typeColumns != -1 ? cursor.getString(1) : getMimeType(filename).toString();
                    } else {
                        filename = "unknown";
                        type = getMimeType(filename).toString();
                    }
                } finally {
                    if (cursor != null) cursor.close();
                }
                break;
            case ContentResolver.SCHEME_FILE:
                filename = new File(uri.getPath()).getName();
                type = getMimeType(filename).toString();
                break;
        }

        return parseUriPart(name, filename, type, uri);
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param filename
     * @param uri
     * @return
     */
    public static MultipartBody.Part parseUriPart(String name, String filename, String type, Uri uri) {
        if (type == null) type = getMimeType(filename).toString();
        return MultipartBody.Part.createFormData(name, filename, parseUriBody(type, uri));
    }

    /**
     * OkHttp 请求头不能是 null/换行符/中文 等一些字符
     * @param text
     * @return
     */
    public static String checkHeaderChar(String text) {
        if (text == null) return "";
        String newValue = text.replace("\n", "");
        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                try {
                    return URLEncoder.encode(newValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    break;
                }
            }
        }
        return newValue;
    }

    /**
     * 尝试获取 RxJava2 的Retrofit适配器
     * @return
     */
    public static CallAdapter.Factory getRxJava2CallAdapterFactory() {
        try {
            Class check = Class.forName("io.reactivex.Observable");
            Class clazz = Class.forName("retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory");
            Object factory = clazz.getMethod("create").invoke(null);
            return (CallAdapter.Factory) factory;
        } catch (Exception ignore) {}
        return null;
    }

    /**
     * 尝试获取 RxJava 的Retrofit适配器
     * @return
     */
    public static CallAdapter.Factory getRxJavaCallAdapterFactory() {
        try {
            Class check = Class.forName("rx.Observable");
            Class clazz = Class.forName("retrofit2.adapter.rxjava.RxJavaCallAdapterFactory");
            Object factory = clazz.getMethod("create").invoke(null);
            return (CallAdapter.Factory) factory;
        } catch (Exception ignore) {}
        return null;
    }

    /**
     * 流拷贝
     * @param inputStream
     * @param outputStream
     */
    private static void copyStream(InputStream inputStream, OutputStream outputStream) {
        try {
            int length;
            byte[] buffer = new byte[4096];
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception ignore) {}
            try {
                if (outputStream != null) outputStream.close();
            } catch (Exception ignore) {}
        }
    }
}
