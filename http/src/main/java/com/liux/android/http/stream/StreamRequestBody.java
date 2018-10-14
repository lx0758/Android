package com.liux.android.http.stream;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

public class StreamRequestBody {

    /** Returns a new request body that transmits {@code content}. */
    public static RequestBody create(final MediaType contentType, final InputStream inputStream) {
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() throws IOException {
                inputStream.reset();
                return inputStream.available();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                inputStream.reset();
                sink.writeAll(Okio.source(inputStream));
            }
        };
    }
}
