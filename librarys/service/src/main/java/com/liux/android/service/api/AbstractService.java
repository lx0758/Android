package com.liux.android.service.api;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public abstract class AbstractService extends Service {
    private final String TAG = getClass().getSimpleName();

    private final BroadcastReceiver mServiceManagerReadyReceiver = new ServiceManagerReadyReceiver();
    private final ServiceConnection mServiceConnection = new SelfServiceConnection();

    private ComponentName mConnectedComponentName;
    private IBinder mConnectedIBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        registerServiceManagerReadyReceiver();

        bindToSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public abstract void onServiceReady();

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

        unregisterServiceManagerReadyReceiver();
    }

    private void registerServiceManagerReadyReceiver() {
        Log.i(TAG, "registerServiceManagerReadyReceiver");
        registerReceiver(
                mServiceManagerReadyReceiver,
                new IntentFilter(ServiceManager.ACTION_SERVICE_MANAGER),
                ServiceManager.getPermission() ,
                null
        );
    }

    private void unregisterServiceManagerReadyReceiver() {
        Log.i(TAG, "unregisterServiceManagerReadyReceiver");
        unregisterReceiver(mServiceManagerReadyReceiver);
    }

    private void bindToSelf() {
        Log.i(TAG, "bindToSelf");
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getClass().getCanonicalName());
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void notifyServiceReady() {
        Log.i(TAG, "notifyServiceReady");
        onServiceReady();
    }

    private class ServiceManagerReadyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            if (mConnectedComponentName != null && mConnectedIBinder != null) {
                ServiceManager.getInstance(context).registerService(
                        mConnectedComponentName,
                        mConnectedIBinder
                );
            }
        }
    }

    private class SelfServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected, name:" + name);
            mConnectedComponentName = name;
            mConnectedIBinder = service;
            if (ServiceManager.getInstance(AbstractService.this).isAvailable()) {
                ServiceManager.getInstance(AbstractService.this).registerService(name, service);
            }
            notifyServiceReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected, name:" + name);
            mConnectedComponentName = null;
            mConnectedIBinder = null;
            if (ServiceManager.getInstance(AbstractService.this).isAvailable()) {
                ServiceManager.getInstance(AbstractService.this).unregisterService(name);
            }
        }
    }
}
