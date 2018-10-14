package com.liux.android.http;

import android.text.TextUtils;

import com.liux.android.http.request.Request;
import com.liux.android.http.stream.StreamPart;
import com.liux.android.http.stream.StreamRequestBody;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;
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
        return Request.isManuallyRequest(request);
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
    public static RequestBody parseXml(String content) {
        return parseString(TYPE_XML.toString(), content);
    }

    /**
     * 生成一个JSON请求体
     * @param content
     * @return
     */
    public static RequestBody parseJson(String content) {
        return parseString(TYPE_JSON.toString(), content);
    }

    /**
     * 生成一个指定类型请求体
     * @param content
     * @return
     */
    public static RequestBody parseString(String content) {
        return parseString(TYPE_TEXT.toString(), content);
    }

    /**
     * 生成一个指定类型请求体
     * @param content
     * @return
     */
    public static RequestBody parseString(String type, String content) {
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
    public static RequestBody parseByte(String type, byte[] bytes) {
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
    public static RequestBody parseInputStream(String type, InputStream inputStream) {
        MediaType mediaType = null;
        if (!TextUtils.isEmpty(type)) {
            mediaType = MediaType.parse(type);
        }
        return StreamRequestBody.create(mediaType, inputStream);
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param type
     * @param string
     * @return
     */
    public static MultipartBody.Part parseStringPart(String name, String type, String string) {
        if (type == null) return StreamPart.createFormData(name, string);
        return StreamPart.createFormData(name, type, string);
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param type
     * @param bytes
     * @return
     */
    public static MultipartBody.Part parseBytePart(String name, String type, byte[] bytes) {
        if (type == null) return StreamPart.createFormData(name, bytes);
        return StreamPart.createFormData(name, type, bytes);
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param name
     * @param type
     * @param inputStream
     * @return
     */
    public static MultipartBody.Part parseInputStreamPart(String name, String type, InputStream inputStream) {
        if (type == null) return StreamPart.createFormData(name, inputStream);
        return StreamPart.createFormData(name, type, inputStream);
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
     * @param fileName
     * @return
     */
    public static MultipartBody.Part parseFilePart(String name, String fileName, File file) {
        MediaType mediaType = getMimeType(file);
        RequestBody body = RequestBody.create(mediaType, file);
        return MultipartBody.Part.createFormData(name, fileName, body);
    }

    /**
     * 字符转JSON
     * @param s
     * @return
     */
    public static String string2Json(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * JSON转字符
     * @param json
     * @return
     */
    public static String json2String(String json) {
        if (json.indexOf('"') == 0) json = json.substring(1);
        if (json.lastIndexOf('"') == json.length() - 1) json = json.substring(0, json.length() - 1);
        return json
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\/", "/")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
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
     * 尝试获取 RxJava 的Retrofit适配器
     * @return
     */
    public static CallAdapter.Factory getRxJavaCallAdapterFactory() {
        try {
            Class check = Class.forName("rx.Observable");
            Class clazz = Class.forName("retrofit2.adapter.rxjava.RxJavaCallAdapterFactory");
            Object factory = clazz.getMethod("create").invoke(null);
            return (CallAdapter.Factory) factory;
        } catch (Exception e) {

        }
        return null;
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
        } catch (Exception e) {

        }
        return null;
    }
}
