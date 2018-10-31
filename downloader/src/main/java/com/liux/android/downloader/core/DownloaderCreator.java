package com.liux.android.downloader.core;

import com.liux.android.downloader.Config;
import com.liux.android.downloader.InitCallback;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 用于专门初始化下载器的类
 */
public class DownloaderCreator {

    private static List<WeakReference<InitCallback>> onInitListeners = new LinkedList<>();

    public static boolean isInit() {
        return DownloaderService.getService() != null;
    }

    public static void init(final Config config) {
        if (DownloaderService.getService() != null) return;
        synchronized(DownloaderCreator.class) {
            if (DownloaderService.getService() != null) return;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DownloaderService.setService(new DownloaderService(config));

                    for (WeakReference<InitCallback> weakReference : onInitListeners) {
                        InitCallback listener = weakReference.get();
                        if (listener != null) listener.onInitialized();
                    }
                    onInitListeners.clear();
                }
            }).start();
        }
    }

    public static void registerInitCallback(InitCallback initCallback) {
        if (initCallback == null) return;

        if (isInit()) {
            initCallback.onInitialized();
            return;
        }

        if (findOnInitListener(initCallback) != null) return;
        onInitListeners.add(new WeakReference<>(initCallback));
    }

    public static void unregisterInitCallback(InitCallback initCallback) {
        if (initCallback == null) return;
        Iterator iterator;
        if ((iterator = findOnInitListener(initCallback)) != null) iterator.remove();
    }

    private static Iterator findOnInitListener(InitCallback listener) {
        Iterator<WeakReference<InitCallback>> iterator = onInitListeners.iterator();
        while (iterator.hasNext()) {
            InitCallback initCallback = iterator.next().get();
            if (initCallback == null) {
                iterator.remove();
                continue;
            }
            if (listener == initCallback) return iterator;
        }
        return null;
    }
}
