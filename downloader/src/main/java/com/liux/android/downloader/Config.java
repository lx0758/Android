package com.liux.android.downloader;

import android.content.Context;
import android.os.Environment;

import com.liux.android.downloader.network.ConnectFactory;

import java.io.File;

public class Config {
    // 最小同时进行任务数
    private static final int MIN_TASK_COUNT = 1;
    // 最大同时进行任务数
    private static final int MAX_TASK_COUNT = 5;

    // 上下文对象
    private Context context;
    // 最大同时允许下载数量
    private int maxTaskCount;
    // 默认存储文件根目录
    private File rootDirectory;
    // 连接工厂实例
    private ConnectFactory connectFactory;

    private Config() {

    }

    public Context getContext() {
        return context;
    }

    public int getMaxTaskCount() {
        return maxTaskCount;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public ConnectFactory getConnectFactory() {
        return connectFactory;
    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public static Builder from(Config config) {
        Builder newBuilder = new Builder(config.context);
        newBuilder.maxTaskCount = config.maxTaskCount;
        newBuilder.rootDirectory = config.rootDirectory;
        newBuilder.connectFactory = config.connectFactory;
        return newBuilder;
    }

    public static class Builder {

        private Context context;
        private int maxTaskCount;
        private File rootDirectory;
        private ConnectFactory connectFactory;

        private Builder(Context context) {
            this.context = context;
        }

        public Builder maxTaskCount(int maxTaskCount) {
            this.maxTaskCount = maxTaskCount;
            return this;
        }

        public Builder rootDirectory(File rootDirectory) {
            this.rootDirectory = rootDirectory;
            return this;
        }

        public Builder connectFactory(ConnectFactory connectFactory) {
            this.connectFactory = connectFactory;
            return this;
        }

        public Config build() {
            Config config = new Config();

            if (context == null) throw new NullPointerException("Context cannot be empty");
            config.context = context.getApplicationContext();

            if (maxTaskCount < MIN_TASK_COUNT) maxTaskCount = MIN_TASK_COUNT;
            if (maxTaskCount > MAX_TASK_COUNT) maxTaskCount = MAX_TASK_COUNT;
            config.maxTaskCount = maxTaskCount;

            if (rootDirectory == null) {
                String path;
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
                } else {
                    path = context.getDir(Environment.DIRECTORY_DOWNLOADS, Context.MODE_PRIVATE).getPath();
                }
                rootDirectory = new File(path + File.separator);
            }
            if (rootDirectory.exists()) {
                if (!rootDirectory.isDirectory()) throw new NullPointerException("rootDirectory not a directory");
            } else {
                rootDirectory.mkdirs();
            }
            config.rootDirectory = rootDirectory;

            if (connectFactory == null) throw new NullPointerException("connectFactory cannot be empty");
            config.connectFactory = connectFactory;

            return config;
        }
    }
}
