package com.liux.android.downloader.core;

import android.os.Handler;
import android.os.Looper;
import com.liux.android.downloader.Config;
import com.liux.android.downloader.InitCallback;

import java.util.LinkedList;
import java.util.List;

/**
 * 用于专门初始化下载器的类
 */
public class DownloaderCreator {

    private static List<InitCallback> initCallbacks = new LinkedList<>();

    public static boolean isInit() {
        return DownloaderService.getService() != null;
    }

    public static void init(final Config config) {
        if (DownloaderService.getService() != null) return;
        synchronized(DownloaderCreator.class) {
            if (DownloaderService.getService() != null) return;

            Looper looper = Looper.myLooper();
            if (looper == null) looper = Looper.getMainLooper();
            final Handler handler = new Handler(looper);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    DownloaderService.setService(
                            new DownloaderService(config)
                    );
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for (InitCallback initCallback : initCallbacks) {
                                initCallback.onInitialized();
                            }
                            initCallbacks.clear();
                        }
                    });
                }
            }).start();
        }
    }

    public static void release() {
        if (DownloaderService.getService() != null) {
            DownloaderService.getService().stopAllTasks();
            DownloaderService.getService().unregisterAllTaskCallback();
            DownloaderService.setService(null);
        }
    }

    public static void registerInitCallback(InitCallback initCallback) {
        if (initCallback == null) return;

        if (isInit()) {
            initCallback.onInitialized();
            return;
        }

        if (initCallbacks.contains(initCallback)) return;
        initCallbacks.add(initCallback);
    }

    public static void unregisterInitCallback(InitCallback initCallback) {
        if (initCallback == null) return;
        initCallbacks.remove(initCallback);
    }
}
