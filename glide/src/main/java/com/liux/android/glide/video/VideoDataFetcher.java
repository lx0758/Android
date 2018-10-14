package com.liux.android.glide.video;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Liux on 2017/9/13.
 */

public class VideoDataFetcher implements DataFetcher<InputStream> {

    private Video mVideo;
    private int mWidth, mHeight;

    private Thread mThread;
    private MediaMetadataRetriever mMediaMetadataRetriever;

    VideoDataFetcher(Video video, int width, int height) {
        mVideo = video;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull final DataCallback<? super InputStream> dataCallback) {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = mVideo.getStringUrl();
                MediaMetadataRetriever retriever = getMediaMetadataRetriever();
                try {
                    if (Build.VERSION.SDK_INT >= 14) {
                        retriever.setDataSource(url, new HashMap<String, String>());
                    } else {
                        retriever.setDataSource(url);
                    }

                    Bitmap bitmap = retriever.getFrameAtTime();
                    if (bitmap == null) {
                        dataCallback.onLoadFailed(new NullPointerException("gets thumbnail failure."));
                        return;
                    }

                    bitmap = scaleUp(bitmap, mWidth, mHeight);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

                    dataCallback.onDataReady(inputStream);
                } catch (Exception e) {
                    dataCallback.onLoadFailed(e);
                } finally {
                    try {
                        if (retriever != null) retriever.release();
                    } catch (Exception ignore) {}
                }
            }
        });
        mThread.start();
    }

    @Override
    public void cleanup() {
        if (mMediaMetadataRetriever != null) {
            mMediaMetadataRetriever.release();
            mMediaMetadataRetriever = null;
        }
        if (mThread != null) {
            if (mThread.isAlive()) mThread.stop();
            mThread = null;
        }
    }

    @Override
    public void cancel() {
        cleanup();
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }

    private MediaMetadataRetriever getMediaMetadataRetriever() {
        if (mMediaMetadataRetriever == null) {
            mMediaMetadataRetriever = new MediaMetadataRetriever();
        }
        return mMediaMetadataRetriever;
    }

    /**
     * 等比缩放
     * @param src
     * @param outWidth
     * @param outHeight
     * @return
     */
    private Bitmap scaleUp(Bitmap src, int outWidth, int outHeight) {
        float width, height;

        float imageWidth = src.getWidth();
        float imageHeight = src.getHeight();

        if (outWidth / imageWidth <= outHeight / imageHeight) {
            width = imageWidth * (outWidth / imageWidth);
            height = imageHeight * (outWidth / imageWidth);
        } else {
            width = imageWidth * (outHeight / imageHeight);
            height = imageHeight * (outHeight / imageHeight);
        }

        return Bitmap.createScaledBitmap(src, (int) width, (int) height, false);
    }
}