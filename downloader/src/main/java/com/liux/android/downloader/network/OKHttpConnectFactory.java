package com.liux.android.downloader.network;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OKHttpConnectFactory implements ConnectFactory {

    private Call.Factory factory;

    public OKHttpConnectFactory() {
        this (null);
    }

    public OKHttpConnectFactory(Call.Factory factory) {
        if (factory == null) {
            factory = new OkHttpClient.Builder().build();
        }
        this.factory = factory;
    }

    @Override
    public Connect create() {
        return new HttpConnect(factory);
    }

    private static class HttpConnect implements Connect {

        private Call call;
        private Call.Factory factory;

        public HttpConnect(Call.Factory factory) {
            this.factory = factory;
        }

        @Override
        public void load(String url, String method, Map<String, List<String>> headers) {
            Headers.Builder builder = new Headers.Builder();
            if (headers != null) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    String name = entry.getKey();
                    List<String> values = entry.getValue();

                    if (values == null || values.isEmpty()) continue;
                    for (String value : values) {
                        builder.add(name, value);
                    }
                }
            }
            Request request = new Request.Builder()
                    .url(url)
                    .method(method, null)
                    .headers(builder.build())
                    .build();
            call = factory.newCall(request);
        }

        @Override
        public boolean isExecuted() {
            return call != null && call.isExecuted();
        }

        @Override
        public ConnectResult execute() throws IOException {
            Response response = call.execute();
            if (!response.isSuccessful()) throw new IOException("execute failure");

            ResponseBody responseBody = response.body();
            if (responseBody == null) throw new IOException("body is null");

            return ConnectResult.create(
                    responseBody.byteStream(),
                    response.headers().toMultimap()
            );
        }

        @Override
        public boolean isCanceled() {
            return call == null || call.isCanceled();
        }


        @Override
        public void cancel() {
            if (call == null) return;
            call.cancel();
        }
    }
}
