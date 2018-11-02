package com.liux.android.downloader.network;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 连接器返回实例
 */
public class ConnectResponse {

    private Connect connect;

    private int code;
    private Map<String, List<String>> headers;

    private InputStream inputStream;

    public static ConnectResponse create(Connect connect, int code, Map<String, List<String>> headers, InputStream inputStream) {
        return new ConnectResponse(connect, code, headers, inputStream);
    }

    public ConnectResponse(Connect connect, int code, Map<String, List<String>> headers, InputStream inputStream) {
        this.connect = connect;
        this.code = code;
        this.headers = headers;
        this.inputStream = inputStream;
    }

    public int code() {
        return code;
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    /**
     * 检查headers是否包含某个header,且一定内容有不为空的值
     * @param name
     * @return
     */
    public boolean hasHeader(String name) {
        if (name == null) return false;
        if (headers == null) return false;
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (!name.toUpperCase().equals(entry.getKey().toUpperCase())) continue;
            if (entry.getValue() == null || entry.getValue().isEmpty()) continue;
            if (!TextUtils.isEmpty(entry.getValue().get(0))) return true;
        }
        return false;
    }

    public List<String> header(String name) {
        if (headers == null) return null;
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (name.toUpperCase().equals(entry.getKey().toUpperCase())) return entry.getValue();
        }
        return null;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public InputStream inputstream() {
        return inputStream;
    }

    public void close() {
        connect.close();
        try {
            inputStream.close();
        } catch (IOException ignore) {}
    }
}
