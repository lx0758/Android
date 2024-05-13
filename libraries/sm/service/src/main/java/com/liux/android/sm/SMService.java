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

    public static void startService(Context context) {
        Log.d(TAG, "startService");
        context.startService(
                new Intent(context, SMService.class)
        );
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mSMInterfaceImpl = new SMInterfaceImpl(getApplicationContext());

        registerPackageChangedReceiver();

        bindToSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mSMInterfaceImpl;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

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
        startModuleService();
    }

    private void startModuleService() {
        Log.i(TAG, "startModuleService");
        Intent moduleActionIntent = new Intent(SMInterface.getModuleServiceAction(this));
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentServices(moduleActionIntent, 0);

        Collections.sort(resolveInfoList, (o1, o2) -> o2.priority - o1.priority);

        Intent moduleServiceIntent;
        String moduleServiceComponent;
        for (ResolveInfo resolveInfo : resolveInfoList) {
            moduleServiceComponent = resolveInfo.serviceInfo.packageName + "/" + resolveInfo.serviceInfo.name;
            moduleServiceIntent = new Intent().setClassName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);

            if (mSMInterfaceImpl.isModuleServiceAvailable(moduleServiceIntent.getComponent())) continue;

            try {
                startService(moduleServiceIntent);
                Log.i(TAG, "startModuleService finish, component:" + moduleServiceComponent);
            } catch (Exception e) {
                Log.e(TAG, "startModuleService exception, component:" + moduleServiceComponent, e);
            }
        }
    }

    private class SelfServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "SelfServiceConnection -> onServiceConnected");
            mSMInterfaceImpl.addModuleService(name, service);
            notifyServiceManagerReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "SelfServiceConnection -> onServiceDisconnected");
            mSMInterfaceImpl.removeModuleService(name);
        }
    }

    private class PackageChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "PackageChangedBroadcastReceiver -> onReceive");
            startModuleService();
        }
    }
}
