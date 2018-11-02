package com.liux.android.downloader.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OKHttp 连接工厂&连接器实现
 */
public class OKHttpConnectFactory implements ConnectFactory {

    private Call.Factory factory;

    public OKHttpConnectFactory() {
        this (null);
    }

    public OKHttpConnectFactory(Call.Factory factory) {
        if (factory == null) {
            factory = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .followSslRedirects(true)
                    .followRedirects(true)
                    .build();
        }
        this.factory = factory;
    }

    @Override
    public Connect create() {
        return new OkHttpConnect(factory);
    }

    /**
     * OKHttp 连接器实现
     */
    private static class OkHttpConnect implements Connect {

        private Call.Factory factory;

        private Call call;
        private Response response;

        OkHttpConnect(Call.Factory factory) {
            this.factory = factory;
        }

        @Override
        public boolean isConnect() {
            return call != null || response != null;
        }

        @Override
        public ConnectResponse connect(String url, String method, Map<String, List<String>> headers, boolean needBody) throws IOException {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .method(method, null);
            if (headers != null) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    String name = entry.getKey();
                    List<String> values = entry.getValue();
                    if (values == null || values.isEmpty()) continue;
                    for (String value : values) {
                        builder.header(name, value);
                    }
                }
            }

            Call call = factory.newCall(builder.build());
            Response response = call.execute();

            InputStream inputStream = null;
            if (needBody) {
                ResponseBody responseBody = response.body();
                if (responseBody == null) throw new IOException("body is null");

                inputStream = responseBody.byteStream();

                this.call = call;
                this.response = response;
            }
            ConnectResponse connectResponse =  ConnectResponse.create(
                    this,
                    response.code(),
                    response.headers().toMultimap(),
                    inputStream
            );

            if (!needBody) {
                call.cancel();
                response.close();
            }

            return connectResponse;
        }

        @Override
        public void close() {
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
            call = null;
            if (response != null) {
                response.close();
            }
            response = null;
        }
    }
}
