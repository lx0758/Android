package com.liux.android.sm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

import com.liux.android.sm.api.SMInterface;

import java.util.Collections;
import java.util.List;

public class SMService extends Service {
    private static final String TAG = "SMService";

    private final ServiceConnection mServiceConnection = new SelfServiceConnection();
    private final BroadcastReceiver mPackageChangedBroadcastReceiver = new PackageChangedBroadcastReceiver();

    private SMInterfaceImpl mSMInterfaceImpl;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        mSMInterfaceImpl = new SMInterfaceImpl(getApplicationContext());

        registerPackageChangedReceiver();

        bindToSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind, intent:" + intent);
        return mSMInterfaceImpl;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        unbindService(mServiceConnection);
        unregisterPackageChangedReceiver();
    }

    private void registerPackageChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        registerReceiver(mPackageChangedBroadcastReceiver, intentFilter);
    }

    private void unregisterPackageChangedReceiver() {
        unregisterReceiver(mPackageChangedBroadcastReceiver);
    }

    private void bindToSelf() {
        Log.d(TAG, "bindToSelf");
        bindService(
                new Intent(getApplicationContext(), SMService.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    private void notifyServiceManagerReady() {
        Log.d(TAG, "notifyServiceManagerReady");
        sendBroadcast(new Intent(SMInterface.getServiceReadyAction(this)), SMInterface.getServicePermission(this));
        bindAllModuleService();
    }

    private void bindAllModuleService() {
        Log.i(TAG, "bindAllModuleService");
        Intent moduleActionIntent = new Intent(SMInterface.getModuleServiceAction(this));
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentServices(moduleActionIntent, 0);

        Collections.sort(resolveInfoList, (o1, o2) -> o2.priority - o1.priority);

        ComponentName componentName;
        for (ResolveInfo resolveInfo : resolveInfoList) {
            componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);

            if (mSMInterfaceImpl.isModuleServiceAvailable(componentName)) continue;

            bindModuleService(componentName);
        }
    }

    private void bindModuleService(ComponentName componentName) {
        try {
            Intent intent = new Intent();
            intent.setComponent(componentName);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG, "bindModuleService finish, component:" + componentName.toShortString());
        } catch (Exception e) {
            Log.e(TAG, "bindModuleService exception, component:" + componentName.toShortString(), e);
        }
    }

    private class SelfServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "SelfServiceConnection#onServiceConnected");
            mSMInterfaceImpl.addModuleService(name, service);
            if (SMService.this.getPackageName().equals(name.getPackageName()) &&
                    SMService.this.getClass().getName().equals(name.getClassName())) {
                notifyServiceManagerReady();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "SelfServiceConnection#onServiceDisconnected");
            mSMInterfaceImpl.removeModuleService(name);
            bindModuleService(name);
        }
    }

    private class PackageChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "PackageChangedBroadcastReceiver#onReceive");
            bindAllModuleService();
        }
    }
}
