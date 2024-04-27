package com.liux.android.example.service;

import android.util.Log;

import com.liux.android.sm.api.ModuleService;

public class BackgroundService extends ModuleService {
    private static final String TAG = "BackgroundService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onModuleServiceReady() {
        super.onModuleServiceReady();
        Log.i(TAG, "onModuleServiceReady");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
