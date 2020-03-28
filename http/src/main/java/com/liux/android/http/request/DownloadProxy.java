package com.liux.android.http.request;

import android.os.Handler;
import android.os.Looper;

import com.liux.android.http.progress.OnResponseProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class DownloadProxy implements OnResponseProgressListener, Callback {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private File mSaveFile;
    private DownloadCallback mDownloadCallback;

    private long mTotalBytesRead, mContentLength;
    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            mDownloadCallback.onProgress(mTotalBytesRead, mContentLength);
            mHandler.postDelayed(mRunable, 200L);
        }
    };

    public DownloadProxy(File saveFile, DownloadCallback downloadCallback) {
        mSaveFile = saveFile;
        mDownloadCallback = downloadCallback;
    }

    @Override
    public void onResponseProgress(HttpUrl httpUrl, final long totalBytesRead, final long contentLength, boolean done) {
        long cache = mContentLength;
        mTotalBytesRead = totalBytesRead;
        mContentLength = contentLength;
        if (cache == 0) mHandler.post(mRunable);
    }

    @Override
    public void onSucceed(Request request, Response response) throws IOException {
        InputStream inputStream = response.body().byteStream();
        OutputStream outputStream = new FileOutputStream(mSaveFile);
        int len = 0;
        byte[] buffer = new byte[2048];
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        mHandler.removeCallbacks(mRunable);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDownloadCallback.onProgress(mTotalBytesRead, mContentLength);
                mDownloadCallback.onSucceed(mSaveFile);
            }
        });
    }

    @Override
    public void onFailure(Request request, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDownloadCallback.onFailure(e);
            }
        });
    }
}
