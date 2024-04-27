package com.liux.android.sm.api;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public abstract class ModuleService extends Service {
    protected final String TAG = getClass().getSimpleName() + "-MS";

    private final SelfServiceConnection mServiceConnection = new SelfServiceConnection();
    private final SelfSMBroadcastReceiver mSMBroadcastReceiver = new SelfSMBroadcastReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        registerSMBroadcastReceiver();

        bindToSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public void onModuleServiceReady() {
        Log.d(TAG, "onModuleServiceReady");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        unregisterSMBroadcastReceiver();
    }

    private void registerSMBroadcastReceiver() {
        Log.d(TAG, "registerSMBroadcastReceiver");
        mSMBroadcastReceiver.registerReceiver(this);
    }

    private void unregisterSMBroadcastReceiver() {
        Log.d(TAG, "unregisterSMBroadcastReceiver");
        mSMBroadcastReceiver.unregisterReceiver(this);
    }

    private void bindToSelf() {
        Log.d(TAG, "bindToSelf");
        Intent intent = new Intent();
        // noinspection DataFlowIssue
        intent.setClassName(getPackageName(), getClass().getCanonicalName());
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void notifyModuleServiceReady() {
        Log.d(TAG, "notifyModuleServiceReady");
        onModuleServiceReady();
    }

    private class SelfServiceConnection implements ServiceConnection {

        private ComponentName mComponentName;
        private IBinder mIBinder;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "SelfServiceConnection -> onServiceConnected, name:" + name);
            mComponentName = name;
            mIBinder = service;

            notifyModuleServiceReady();
            if (SMInterface.getInstance(ModuleService.this).isAvailable()) {
                SMInterface.getInstance(ModuleService.this).registerService(name, service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "SelfServiceConnection -> onServiceDisconnected, name:" + name);
            mComponentName = null;
            mIBinder = null;

            if (SMInterface.getInstance(ModuleService.this).isAvailable()) {
                SMInterface.getInstance(ModuleService.this).unregisterService(name);
            }
        }

        public boolean isConnected() {
            return mComponentName != null && mIBinder != null;
        }

        public ComponentName getComponentName() {
            return mComponentName;
        }

        public IBinder getIBinder() {
            return mIBinder;
        }
    }

    private class SelfSMBroadcastReceiver extends SMBroadcastReceiver {
        @Override
        public void onServiceManagerReady(Context context, Intent intent) {
            Log.d(TAG, "SelfSMBroadcastReceiver -> onServiceManagerReady");
            SMInterface.getInstance(context).registerService(
                    mServiceConnection.getComponentName(),
                    mServiceConnection.getIBinder()
            );
        }
    }
}
