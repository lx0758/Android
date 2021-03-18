package com.liux.android.framework.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.liux.android.framework.ui.provider.UIProvider;

/**
 * 2018/2/11
 * By Liux
 * lx0758@qq.com
 */

public abstract class BaseApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Application mApplication;

    private static UIProvider mUIProvider;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mApplication = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mUIProvider = initUIProvide();
    }

    public static Context getApplication() {
        return mApplication;
    }

    public static Context getContext() {
        return mApplication;
    }

    public static UIProvider getUIProvide() {
        return mUIProvider;
    }

    protected abstract UIProvider initUIProvide();
}
