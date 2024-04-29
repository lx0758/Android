package com.liux.android.sm.api;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SMInterface {
    private static final String TAG = "SMInterface";

    public static final String META_DATA_KEY_SM_SERVICE_COMPONENT_PREFIX = "sm_service_component_prefix";
    public static final String META_DATA_KEY_SM_MODULE_SERVICE_COMPONENT_PREFIX = "sm_module_service_component_prefix";

    public static final String PERMISSION_ACCESS_SM_SERVICE_SUFFIX = ".permission.ACCESS_SM_SERVICE";

    public static final String ACTION_SM_SERVICE_SUFFIX = ".action.SM_SERVICE";
    public static final String ACTION_SM_SERVICE_READY_SUFFIX = ".action.SM_SERVICE_READY";
    public static final String ACTION_SM_MODULE_INTERFACE_CHANGED_SUFFIX = ".action.SM_MODULE_INTERFACE_CHANGED";
    public static final String ACTION_SM_MODULE_SERVICE_SUFFIX = ".action.SM_MODULE_SERVICE";

    public static final String EXTRA_COMPONENT_NAME = "EXTRA_OPTION";
    public static final String EXTRA_STATUS = "EXTRA_STATUS";
    public static final int EXTRA_STATUS_READY = 1;
    public static final int EXTRA_STATUS_UNREADY = 2;

    @SuppressLint("StaticFieldLeak")
    private volatile static SMInterface sInstance;
    public static SMInterface getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SMInterface.class) {
                if (sInstance == null) {
                    sInstance = new SMInterface(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    @NonNull
    public static String getServicePermission(Context context) {
        return getServiceComponentInfo(context, PERMISSION_ACCESS_SM_SERVICE_SUFFIX);
    }

    @NonNull
    public static String getServiceAction(Context context) {
        return getServiceComponentInfo(context, ACTION_SM_SERVICE_SUFFIX);
    }

    @NonNull
    public static String getServiceReadyAction(Context context) {
        return getServiceComponentInfo(context, ACTION_SM_SERVICE_READY_SUFFIX);
    }

    @NonNull
    public static String getModuleInterfaceChangedAction(Context context) {
        return getServiceComponentInfo(context, ACTION_SM_MODULE_INTERFACE_CHANGED_SUFFIX);
    }

    @NonNull
    public static String getModuleServiceAction(Context context) {
        return SMUtil.getMetaData(context, META_DATA_KEY_SM_MODULE_SERVICE_COMPONENT_PREFIX) + ACTION_SM_MODULE_SERVICE_SUFFIX;
    }

    @NonNull
    private static String getServiceComponentInfo(Context context, String suffix) {
        return SMUtil.getMetaData(context, META_DATA_KEY_SM_SERVICE_COMPONENT_PREFIX) + suffix;
    }

    private final Context mContext;
    private final SMBroadcastReceiver mSMBroadcastReceiver = new SelfBroadcastReceiver();
    private final IBinder.DeathRecipient mDeathRecipient = new SelfDeathRecipient();

    private ISMInterface mSMInterfaceCache;

    private SMInterface(Context context) {
        mContext = context;

        registerSMBroadcastReceiver();
    }

    public boolean isAvailable() {
        return getSMInterface() != null;
    }

    @Nullable
    public IBinder getService(ComponentName componentName) {
        IBinder result = null;
        check: {
            if (componentName == null) {
                Log.e(TAG, "getService, componentName is null");
                break check;
            }

            ISMInterface iSMInterface = getSMInterface();
            if (iSMInterface == null) {
                Log.e(TAG, "getService, server is null");
                break check;
            }

            try {
                result = iSMInterface.getService(componentName);
            } catch (RemoteException e) {
                Log.e(TAG, "getService, exception", e);
            }
        }
        Log.d(TAG, "getService, componentName:" + componentName + ", result:" + (result != null ? "not null" : "null"));
        return result;
    }

    public void registerService(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "registerService, componentName:" + componentName + ", iBinder:" + (iBinder != null ? "not null" : "null"));

        if (componentName == null) {
            Log.e(TAG, "registerService, componentName is null");
            return;
        }

        if (iBinder == null) {
            Log.e(TAG, "registerService, iBinder is null");
            return;
        }

        ISMInterface iSMInterface = getSMInterface();
        if (iSMInterface == null) {
            Log.e(TAG, "registerService, server is null");
            return;
        }

        try {
            iSMInterface.registerService(componentName, iBinder);
        } catch (RemoteException e) {
            Log.e(TAG, "registerService, exception", e);
        }
    }

    public void unregisterService(ComponentName componentName) {
        Log.d(TAG, "unregisterService, componentName:" + componentName);

        if (componentName == null) {
            Log.e(TAG, "unregisterService, componentName is null");
            return;
        }

        ISMInterface iSMInterface = getSMInterface();
        if (iSMInterface == null) {
            Log.e(TAG, "unregisterService, server is null");
            return;
        }

        try {
            iSMInterface.unregisterService(componentName);
        } catch (RemoteException e) {
            Log.e(TAG, "unregisterService, exception", e);
        }
    }

    private void registerSMBroadcastReceiver() {
        mSMBroadcastReceiver.registerReceiver(mContext);
    }

    private ISMInterface getSMInterface() {
        check: {
            if (mSMInterfaceCache != null) {
                break check;
            }

            ComponentName componentName = SMUtil.getServiceComponentName(
                    mContext,
                    null,
                    getServiceAction(mContext)
            );
            if (componentName == null) {
                Log.e(TAG, "getServiceManagerImpl, componentName is null");
                break check;
            }

            Intent intent = new Intent();
            intent.setComponent(componentName);
            IBinder iBinder = mSMBroadcastReceiver.peekService(mContext, intent);
            if (iBinder == null) {
                Log.w(TAG, "getServiceManagerImpl, iBinder is null");
                startServiceManagerService(intent);
                break check;
            }

            try {
                iBinder.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                Log.e(TAG, "getServiceManagerImpl, linkToDeath exception", e);
                break check;
            }

            mSMInterfaceCache = ISMInterface.Stub.asInterface(iBinder);
        }

        Log.d(TAG, "getServiceManagerImpl, result:" + (mSMInterfaceCache != null ? "not null" : "null"));
        return mSMInterfaceCache;
    }

    private void startServiceManagerService(Intent intent) {
        Log.d(TAG, "startServiceManagerService");
        try {
            mContext.startService(intent);
        } catch (Exception e) {
            Log.e(TAG, "startServiceManagerService, exception", e);
        }
    }

    private class SelfBroadcastReceiver extends SMBroadcastReceiver {
        @Override
        public void onServiceManagerReady(Context context, Intent intent) {
            super.onServiceManagerReady(context, intent);
            Log.i(TAG, "SelfBroadcastReceiver -> onReceive(Ready)");
            mDeathRecipient.binderDied();
            getSMInterface();
        }
    }

    private class SelfDeathRecipient implements IBinder.DeathRecipient {
        @Override
        public void binderDied() {
            Log.w(TAG, "SelfDeathRecipient -> binderDied");
            if (mSMInterfaceCache != null) {
                mSMInterfaceCache.asBinder().unlinkToDeath(mDeathRecipient, 0);
                mSMInterfaceCache = null;
            }
        }
    }
}
