package com.liux.android.http.progress;

import com.liux.android.http.wrapper.AbstractRequestBody;
import com.liux.android.http.wrapper.WrapperRequestBody;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

/**
 * 2018/2/27
 * By Liux
 * lx0758@qq.com
 */

public class ProgressRequestBody extends AbstractRequestBody implements WrapperRequestBody {

    private HttpUrl mHttpUrl;
    private RequestBody mRequestBody;
    private OnRequestProgressListener mOnRequestProgressListener;

    private BufferedSink mWrapperBufferedSink;

    public ProgressRequestBody(HttpUrl httpUrl, RequestBody requestBody, OnRequestProgressListener onRequestProgressListener) {
        super(requestBody);
        mHttpUrl = httpUrl;
        mRequestBody = requestBody;
        mOnRequestProgressListener = onRequestProgressListener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mWrapperBufferedSink == null) {
            mWrapperBufferedSink = Okio.buffer(new WrapperForwardingSink(sink, mHttpUrl, mRequestBody, mOnRequestProgressListener));
            mRequestBody.writeTo(mWrapperBufferedSink);
            mWrapperBufferedSink.flush();
        } else {
            mRequestBody.writeTo(sink);
        }
    }

    private static class WrapperForwardingSink extends ForwardingSink {

        private HttpUrl mHttpUrl;
        private RequestBody mRequestBody;
        private OnRequestProgressListener mOnRequestProgressListener;

        private long mTransmittedLength = 0L;
        private long mTotalLength = -1L;
        private boolean mCallCompleted = false;

        public WrapperForwardingSink(BufferedSink sink, HttpUrl httpUrl, RequestBody requestBody, OnRequestProgressListener onRequestProgressListener) {
            super(sink);
            mHttpUrl = httpUrl;
            mRequestBody = requestBody;
            mOnRequestProgressListener = onRequestProgressListener;
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            // 获取总长度
            if (mTotalLength == -1) mTotalLength = mRequestBody.contentLength();

            // 增加当前写入的字节数
            mTransmittedLength += byteCount;

            // 是否完成标志
            boolean completed = mTransmittedLength == mTotalLength;

            // 回调进度
            if (!mCallCompleted) {
                mOnRequestProgressListener.onRequestProgress(mHttpUrl, mTransmittedLength, mTotalLength, completed);
                if (completed) mCallCompleted = true;
            }
        }
    }
}
