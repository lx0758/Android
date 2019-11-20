package com.liux.android.glide.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Liux on 2017/9/13.
 */

public class VideoDataFetcher implements DataFetcher<InputStream> {

    private Context context;
    private Video video;
    private int width, height;

    VideoDataFetcher(Context context, Video video, int width, int height, Options options) {
        this.context = context;
        this.video = video;
        this.width = width;
        this.height = height;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull final DataCallback<? super InputStream> dataCallback) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            Uri uri = video.getUri();
            String scheme = uri.getScheme();
            if(scheme == null || scheme.length() == 0 || scheme.equals("file")) {
                mediaMetadataRetriever.setDataSource(uri.getPath());
            } else if (scheme.equals("http") || scheme.equals("https") || scheme.equals("ftp")){
                mediaMetadataRetriever.setDataSource(uri.toString(), new HashMap<String, String>());
            } else {
                mediaMetadataRetriever.setDataSource(context, uri);
            }

            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(0);
            if (bitmap == null) throw new NullPointerException("gets thumbnail failure.");

            Bitmap scaleBitmap = scaleUp(bitmap, width, height);
            bitmap.recycle();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            scaleBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            dataCallback.onDataReady(inputStream);
        } catch (Exception e) {
            dataCallback.onLoadFailed(e);
        } finally {
            mediaMetadataRetriever.release();
        }
    }

    @Override
    public void cleanup() {

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