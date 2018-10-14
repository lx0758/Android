package com.liux.android.rx.error;

import java.util.ArrayList;
import java.util.List;

/**
 * 2018/2/28
 * By Liux
 * lx0758@qq.com
 */

public class ErrorHandlerManager {

    private static List<OnErrorListener> mOnErrorListeners = new ArrayList<>();

    static void handler(Throwable throwable) {
        for (OnErrorListener listener : mOnErrorListeners) {
            listener.onError(throwable);
        }
    }

    public static void registerListener(OnErrorListener listener) {
        if (mOnErrorListeners.indexOf(listener) == -1) {
            mOnErrorListeners.add(listener);
        }
    }

    public static void unregisterListener(OnErrorListener listener) {
        mOnErrorListeners.remove(listener);
    }
}
