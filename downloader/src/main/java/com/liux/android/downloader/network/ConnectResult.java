package com.liux.android.downloader.network;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ConnectResult {

    private InputStream inputStream;
    private Map<String, List<String>> headers;

    public static ConnectResult create(InputStream inputStream, Map<String, List<String>> headers) {
        return new ConnectResult(inputStream, headers);
    }

    public ConnectResult(InputStream inputStream, Map<String, List<String>> headers) {
        this.inputStream = inputStream;
        this.headers = headers;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }
}
