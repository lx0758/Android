package com.liux.android.glide.video;

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

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull Video video, int width, int height, @NonNull Options options) {
        return new LoadData<InputStream>(
                video,
                new VideoDataFetcher(video, width, height)
        );
    }

    @Override
    public boolean handles(@NonNull Video video) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<Video, InputStream> {
        @NonNull
        @Override
        public ModelLoader<Video, InputStream> build(@NonNull MultiModelLoaderFactory multiModelLoaderFactory) {
            return new VideoModelLoader();
        }

        @Override
        public void teardown() {

        }
    }
}