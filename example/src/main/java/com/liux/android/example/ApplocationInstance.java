package com.liux.android.example;

import android.app.Application;
import android.content.Context;

import com.liux.android.tool.TT;

/**
 * Created by Liux on 2017/8/16.
 */

public class ApplocationInstance extends Application {

    private static Application mApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mApplication = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* 初始化 Toast */
        TT.setContext(getApplicationContext());
    }

    public static Application getApplication() {
        return mApplication;
    }
}
