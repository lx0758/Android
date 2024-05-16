package com.liux.android.example.service;

import android.util.Log;

import com.liux.android.sm.api.ModuleService;

public class BackgroundService extends ModuleService {
    private static final String TAG = "BackgroundService";

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
