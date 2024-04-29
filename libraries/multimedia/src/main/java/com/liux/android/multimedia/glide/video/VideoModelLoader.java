package com.liux.android.multimedia.glide.video;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

/**
 * Created by Liux on 2017/9/13.
 */

public class VideoModelLoader implements ModelLoader<Video, InputStream> {

    private Context context;

    public VideoModelLoader(Context context, MultiModelLoaderFactory multiModelLoaderFactory) {
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull Video video, int width, int height, @NonNull Options options) {
        return new LoadData<InputStream>(
                video,
                new VideoDataFetcher(context, video, width, height, options)
        );
    }

    @Override
    public boolean handles(@NonNull Video video) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<Video, InputStream> {

        private Context context;

        public Factory(Context context) {
            this.context = context.getApplicationContext();
        }

        @NonNull
        @Override
        public ModelLoader<Video, InputStream> build(@NonNull MultiModelLoaderFactory multiModelLoaderFactory) {
            return new VideoModelLoader(context, multiModelLoaderFactory);
        }

        @Override
        public void teardown() {

        }
    }
}