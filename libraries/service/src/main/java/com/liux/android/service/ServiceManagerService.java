package com.liux.android.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.liux.android.service.api.ServiceManager;

import java.util.List;

public class ServiceManagerService extends Service {
    private static final String TAG = "ServiceManagerService";

    private final ServiceConnection mServiceConnection = new SelfServiceConnection();

    private ServiceManagerInterfaceImpl mIServiceManagerInterface;

    public static void startService(Context context) {
        Log.i(TAG, "startService");
        context.startService(
                new Intent(context, ServiceManagerService.class)
        );
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        mIServiceManagerInterface = new ServiceManagerInterfaceImpl(getApplicationContext());

        bindToSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mIServiceManagerInterface;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onServiceReady() {
        Log.i(TAG, "onServiceReady");
        sendBroadcast(new Intent(ServiceManager.ACTION_SERVICE_MANAGER));

        startAllServiceModule();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void bindToSelf() {
        Log.i(TAG, "bindToSelf");
        bindService(
                new Intent(getApplicationContext(), ServiceManagerService.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    private void notifyServiceReady() {
        Log.i(TAG, "notifyServiceReady");
        onServiceReady();
    }

    private void startAllServiceModule() {
        Log.i(TAG, "startAllServiceModule");
        Intent moduleActionIntent = new Intent(ServiceManager.ACTION_SERVICE_MANAGER_MODULE);
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentServices(moduleActionIntent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            try {
                Intent moduleServiceIntent = new Intent(ServiceManager.ACTION_SERVICE_MANAGER_MODULE);
                moduleServiceIntent.setClassName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
                startService(moduleServiceIntent);
            } catch (RuntimeException e) {
                Log.e(TAG, "startAllServiceModule, exception", e);
            }
        }
    }

    private class SelfServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "SelfServiceConnection -> onServiceConnected");
            try {
                mIServiceManagerInterface.registerService(name, service);
            } catch (RemoteException e) {
                Log.e(TAG, "onServiceConnected, exception", e);
            }
            notifyServiceReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "SelfServiceConnection -> onServiceDisconnected");
            try {
                mIServiceManagerInterface.unregisterService(name);
            } catch (RemoteException e) {
                Log.e(TAG, "onServiceDisconnected, exception", e);
            }
        }
    }
}
