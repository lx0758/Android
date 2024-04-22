package com.liux.android.service;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.liux.android.service.api.IServiceManagerInterface;
import com.liux.android.service.api.SMUtil;
import com.liux.android.service.api.ServiceManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManagerInterfaceImpl extends IServiceManagerInterface.Stub {
    private static final String TAG = "SMInterfaceImpl";

    private final Context mContext;
    private final Map<ComponentName, IBinder> mIBinderMap = new ConcurrentHashMap<>();

    public ServiceManagerInterfaceImpl(Context context) {
        mContext = context;
    }

    @Override
    public IBinder getService(ComponentName componentName) throws RemoteException {
        Log.i(TAG, "getService, componentName:" + componentName);
        SMUtil.checkPermission(mContext, ServiceManager.getPermission());

        try {
            return mIBinderMap.get(componentName);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public void registerService(ComponentName componentName, IBinder iBinder) throws RemoteException {
        Log.i(TAG, "registerService, componentName:" + componentName);
        SMUtil.checkPermission(mContext, ServiceManager.getPermission());

        iBinder.linkToDeath(new SelfDeathRecipient(componentName), 0);
        mIBinderMap.put(componentName, iBinder);
    }

    @Override
    public void unregisterService(ComponentName componentName) throws RemoteException {
        Log.i(TAG, "unregisterService, componentName:" + componentName);
        SMUtil.checkPermission(mContext, ServiceManager.getPermission());

        mIBinderMap.remove(componentName);
    }

    private class SelfDeathRecipient implements DeathRecipient {

        private final ComponentName mComponentName;

        public SelfDeathRecipient(ComponentName componentName) {
            mComponentName = componentName;
        }

        @Override
        public void binderDied() {
            Log.i(TAG, "binderDied, componentName:" + mComponentName);
            mIBinderMap.remove(mComponentName);
        }
    }
}
