package com.liux.android.downloader.network;

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

    public int getCode() {
        return code;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void close() {
        connect.close();
    }
}
