package com.liux.android.service.api;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ServiceManager {
    private static final String TAG = "ServiceManager";

    public static final String PERMISSION_ACCESS_SERVICE_MANAGER = "com.liux.android.service.permission.ACCESS_SERVICE_MANAGER";

    public static final String ACTION_SERVICE_MANAGER = "com.liux.android.service.action.SERVICE_MANAGER";
    public static final String ACTION_SERVICE_MANAGER_MODULE = "com.liux.android.service.action.SERVICE_MANAGER_MODULE";

    @SuppressLint("StaticFieldLeak")
    private volatile static ServiceManager mInstance;
    public static ServiceManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ServiceManager.class) {
                if (mInstance == null) {
                    mInstance = new ServiceManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public static String getPermission() {
        return PERMISSION_ACCESS_SERVICE_MANAGER;
    }

    private final Context mContext;
    private final IBinder.DeathRecipient mDeathRecipient = new SelfDeathRecipient();

    private IBinder mIBinderCache;

    private ServiceManager(Context context) {
        mContext = context;
    }

    public boolean isAvailable() {
        return getServiceManagerInterface() != null;
    }

    public IBinder getService(ComponentName componentName) {
        Log.i(TAG, "getService, componentName:" + componentName);

        if (componentName == null) {
            Log.e(TAG, "getService, componentName is null");
            return null;
        }

        IServiceManagerInterface iServiceManagerInterface = getServiceManagerInterface();
        if (iServiceManagerInterface == null) {
            Log.e(TAG, "getService, server is null");
            return null;
        }

        try {
            return iServiceManagerInterface.getService(componentName);
        } catch (RemoteException e) {
            Log.e(TAG, "getService, exception", e);
            return null;
        }
    }

    public void registerService(ComponentName componentName, IBinder iBinder) {
        Log.i(TAG, "registerService, componentName:" + componentName);

        if (componentName == null) {
            Log.e(TAG, "registerService, componentName is null");
            return;
        }

        IServiceManagerInterface iServiceManagerInterface = getServiceManagerInterface();
        if (iServiceManagerInterface == null) {
            Log.e(TAG, "registerService, server is null");
            return;
        }

        try {
            iServiceManagerInterface.registerService(componentName, iBinder);
        } catch (RemoteException e) {
            Log.e(TAG, "registerService, exception", e);
        }
    }

    public void unregisterService(ComponentName componentName) {
        Log.i(TAG, "unregisterService, componentName:" + componentName);

        if (componentName == null) {
            Log.e(TAG, "unregisterService, componentName is null");
            return;
        }

        IServiceManagerInterface iServiceManagerInterface = getServiceManagerInterface();
        if (iServiceManagerInterface == null) {
            Log.e(TAG, "unregisterService, server is null");
            return;
        }

        try {
            iServiceManagerInterface.unregisterService(componentName);
        } catch (RemoteException e) {
            Log.e(TAG, "unregisterService, exception", e);
        }
    }

    private IServiceManagerInterface getServiceManagerInterface() {
        Log.i(TAG, "getServiceManagerInterface");

        if (mIBinderCache == null) {
            ComponentName componentName = SMUtil.getServiceComponentName(
                    mContext,
                    null,
                    ServiceManager.ACTION_SERVICE_MANAGER
            );
            if (componentName == null) {
                Log.e(TAG, "getServiceManagerInterface, componentName is null");
                return null;
            }

            Intent intent = new Intent();
            intent.setComponent(componentName);
            IBinder iBinder = PeekBroadcastReceiver.peek(mContext, intent);
            if (iBinder == null) {
                Log.i(TAG, "getServiceManagerInterface, iBinder is null");
                startServiceManagerService(intent);
                return null;
            }

            try {
                iBinder.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                Log.e(TAG, "getServiceManagerInterface, linkToDeath exception", e);
                return null;
            }
            mIBinderCache = iBinder;
        }

        return IServiceManagerInterface.Stub.asInterface(mIBinderCache);
    }

    private void startServiceManagerService(Intent intent) {
        Log.i(TAG, "startServiceManagerService");
        try {
            mContext.startService(intent);
        } catch (Exception e) {
            Log.i(TAG, "startServiceManagerService, exception", e);
        }
    }

    private static class PeekBroadcastReceiver extends BroadcastReceiver {

        private static final PeekBroadcastReceiver INSTANCE = new PeekBroadcastReceiver();
        public static IBinder peek(Context context, Intent intent) {
            return INSTANCE.peekService(context, intent);
        }

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    private class SelfDeathRecipient implements IBinder.DeathRecipient {
        @Override
        public void binderDied() {
            if (mIBinderCache != null) {
                mIBinderCache.unlinkToDeath(mDeathRecipient, 0);
                mIBinderCache = null;
            }
        }
    }
}
