package com.liux.android.http.request;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.liux.android.http.progress.OnResponseProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class DownloadProxy implements Callback {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private File mSaveFile;
    private DownloadCallback mDownloadCallback;

    private boolean mCompleted = false;
    private long mTransmittedLength, mTotalLength;

    public DownloadProxy(File saveFile, DownloadCallback downloadCallback) {
        mSaveFile = saveFile;
        mDownloadCallback = downloadCallback;
    }

    @Override
    public void onSucceed(Request request, Response response) throws IOException {
        mTotalLength = response.body().contentLength();
        InputStream inputStream = response.body().byteStream();
        OutputStream outputStream = new FileOutputStream(mSaveFile);
        int len = 0;
        boolean firstCallProgress = true;
        byte[] buffer = new byte[8192];
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
            mTransmittedLength += len;

            if (firstCallProgress) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCompleted) return;
                        mDownloadCallback.onProgress(mTransmittedLength, mTotalLength);
                        mHandler.postDelayed(this, 200L);
                    }
                });
                firstCallProgress = false;
            }
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        mCompleted = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDownloadCallback.onProgress(mTransmittedLength, mTotalLength);
                mDownloadCallback.onSucceed(mSaveFile);
            }
        });
    }

    @Override
    public void onFailure(Request request, final IOException e) {
        mCompleted = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDownloadCallback.onFailure(e);
            }
        });
    }
}
