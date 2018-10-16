package com.liux.android.downloader;

import android.content.Context;

import com.liux.android.downloader.network.ConnectFactory;

import java.io.File;

public class DownloaderConfig {

    private DownloaderConfig() {

    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public static Builder from(Builder builder) {
        Builder newBuilder = new Builder(builder.context);
        return newBuilder;
    }

    public static class Builder {

        private Context context;
        private int threadCount;
        private File rootDirectory;
        private ConnectFactory connectFactory;

        private Builder(Context context) {
            this.context = context;
        }

        public Builder connectFactory(ConnectFactory connectFactory) {
            this.connectFactory = connectFactory;
            return this;
        }

        public Builder rootDirectory(File rootDirectory) {
            this.rootDirectory = rootDirectory;
            return this;
        }

        public Builder threadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public DownloaderConfig build() {
            return new DownloaderConfig();
        }
    }
}
