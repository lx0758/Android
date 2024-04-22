package com.liux.android.http.progress;

import com.liux.android.http.wrapper.AbstractResponseBody;
import com.liux.android.http.wrapper.WrapperResponseBody;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * 2018/2/27
 * By Liux
 * lx0758@qq.com
 */

public class ProgressResponseBody extends AbstractResponseBody implements WrapperResponseBody {

    private HttpUrl mHttpUrl;
    private ResponseBody mResponseBody;
    private OnResponseProgressListener mResponseProgressListener;

    private BufferedSource mWrapperBufferedSource;
    
    public ProgressResponseBody(HttpUrl httpUrl, ResponseBody responseBody, OnResponseProgressListener onResponseProgressListener) {
        super(responseBody);
        mHttpUrl = httpUrl;
        mResponseBody = responseBody;
        mResponseProgressListener = onResponseProgressListener;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mWrapperBufferedSource == null) {
            mWrapperBufferedSource = Okio.buffer(new WrapperForwardingSource(mHttpUrl, mResponseBody, mResponseProgressListener));
            return mWrapperBufferedSource;
        } else {
            return mResponseBody.source();
        }
    }

    private static class WrapperForwardingSource extends ForwardingSource {

        private HttpUrl mHttpUrl;
        private ResponseBody mResponseBody;
        private OnResponseProgressListener mResponseProgressListener;

        private long mTransmittedLength = 0L;
        private long mTotalLength = -1L;
        private boolean mCallCompleted = false;

        public WrapperForwardingSource(HttpUrl httpUrl, ResponseBody responseBody, OnResponseProgressListener onResponseProgressListener) {
            super(responseBody.source());
            mHttpUrl = httpUrl;
            mResponseBody = responseBody;
            mResponseProgressListener = onResponseProgressListener;

            mTotalLength = mResponseBody.contentLength();
        }

        @Override
        public long read(Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);

            // 是否完成标志
            boolean completed = bytesRead == -1;

            // 增加当前读取的字节数，如果读取完成了 bytesRead 会返回 -1
            if (!completed) mTransmittedLength += bytesRead;

            // 回调进度
            if (!mCallCompleted) {
                mResponseProgressListener.onResponseProgress(mHttpUrl, mTransmittedLength, mTotalLength, completed);
                if (completed) mCallCompleted = true;
            }

            return bytesRead;
        }
    }
}
