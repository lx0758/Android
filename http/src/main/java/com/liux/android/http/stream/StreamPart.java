package com.liux.android.http.stream;

import java.io.InputStream;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 支持 byte[] 和 输入流的Part
 */
public class StreamPart {
    public static final MediaType TYPE_STREAM = MediaType.parse("application/octet-stream");
    public static final MediaType TYPE_TEXT = MediaType.parse("text/plain;charset=UTF-8");

    public static MultipartBody.Part createFormData(String name, String string) {
        return createFormData(name, TYPE_TEXT.toString(), string);
    }

    public static MultipartBody.Part createFormData(String name, String type, String string) {
        MediaType contentType = null;
        if (type != null) {
            contentType = MediaType.parse(type);
        }
        return createFormData(name, RequestBody.create(contentType, string));
    }

    public static MultipartBody.Part createFormData(String name, byte[] bytes) {
        return createFormData(name, TYPE_STREAM.toString(), bytes);
    }

    public static MultipartBody.Part createFormData(String name, String type, byte[] bytes) {
        MediaType contentType = null;
        if (type != null) {
            contentType = MediaType.parse(type);
        }
        return createFormData(name, RequestBody.create(contentType, bytes));
    }

    public static MultipartBody.Part createFormData(String name, InputStream inputStream) {
        return createFormData(name, TYPE_STREAM.toString(), inputStream);
    }

    public static MultipartBody.Part createFormData(String name, String type, InputStream inputStream) {
        MediaType contentType = null;
        if (type != null) {
            contentType = MediaType.parse(type);
        }
        return createFormData(name, StreamRequestBody.create(contentType, inputStream));
    }

    private static MultipartBody.Part createFormData(String name, RequestBody body) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        StringBuilder disposition = new StringBuilder("form-data; name=");
        appendQuotedString(disposition, name);

        return MultipartBody.Part.create(
                new Headers.Builder()
                        .add("Content-Disposition", disposition.toString())
                        .add("Content-Transfer-Encoding", "binary")
                        .build(),
                body
        );
    }

    /**
     * 引用自 {@link MultipartBody}
     * @param target
     * @param key
     * @return
     */
    private static StringBuilder appendQuotedString(StringBuilder target, String key) {
        target.append('"');
        for (int i = 0, len = key.length(); i < len; i++) {
            char ch = key.charAt(i);
            switch (ch) {
                case '\n':
                    target.append("%0A");
                    break;
                case '\r':
                    target.append("%0D");
                    break;
                case '"':
                    target.append("%22");
                    break;
                default:
                    target.append(ch);
                    break;
            }
        }
        target.append('"');
        return target;
    }
}
