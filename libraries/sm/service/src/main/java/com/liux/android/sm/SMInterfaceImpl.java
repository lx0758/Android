package com.liux.android.sm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.liux.android.sm.api.ISMInterface;
import com.liux.android.sm.api.SMUtil;
import com.liux.android.sm.api.SMInterface;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class SMInterfaceImpl extends ISMInterface.Stub {
    private static final String TAG = "SMInterfaceImpl";

    private final Context mContext;
    private final Map<ComponentName, BinderWrapper> mBinderMap = new ConcurrentHashMap<>();

    public SMInterfaceImpl(Context context) {
        mContext = context;
    }

    @Override
    public IBinder getService(ComponentName componentName) throws RemoteException {
        Log.i(TAG, "getService, componentName:" + componentName);
        SMUtil.checkPermission(mContext, SMInterface.getServicePermission(mContext));

        BinderWrapper binderWrapper = mBinderMap.get(componentName);
        if (binderWrapper != null) {
            return binderWrapper.iBinder;
        }
        return null;
    }

    @Override
    public void registerService(ComponentName componentName, IBinder iBinder) throws RemoteException {
        Log.i(TAG, "registerService, componentName:" + componentName + ", iBinder:" + (iBinder != null ? "not null" : "null"));
        SMUtil.checkPermission(mContext, SMInterface.getServicePermission(mContext));

        if (iBinder != null) {
            addModuleService(componentName, iBinder);
        }
    }

    @Override
    public void unregisterService(ComponentName componentName) throws RemoteException {
        Log.i(TAG, "unregisterService, componentName:" + componentName);
        SMUtil.checkPermission(mContext, SMInterface.getServicePermission(mContext));

        removeModuleService(componentName);
    }

    boolean isServiceAlive(ComponentName componentName) {
        BinderWrapper binderWrapper = mBinderMap.get(componentName);
        return binderWrapper != null && binderWrapper.iBinder != null && binderWrapper.iBinder.isBinderAlive();
    }

    void addModuleService(ComponentName componentName, IBinder iBinder) {
        try {
            DeathRecipient deathRecipient = new SelfDeathRecipient(componentName);
            iBinder.linkToDeath(deathRecipient, 0);
            mBinderMap.put(componentName, new BinderWrapper(iBinder, deathRecipient));
            notifyModuleServiceChanged(componentName, SMInterface.EXTRA_STATUS_READY);
        } catch (RemoteException e) {
            Log.i(TAG, "addModuleService, exception", e);
        }
    }

    protected void removeModuleService(ComponentName componentName) {
        BinderWrapper binderWrapper = mBinderMap.remove(componentName);
        if (binderWrapper != null) {
            try {
                binderWrapper.iBinder.unlinkToDeath(binderWrapper.deathRecipient, 0);
            } catch (NoSuchElementException ignored) {}
        }
        notifyModuleServiceChanged(componentName, SMInterface.EXTRA_STATUS_UNREADY);
    }

    private void notifyModuleServiceChanged(ComponentName componentName, int status) {
        Log.i(TAG, "notifyModuleServiceChanged, componentName:" + componentName + ", status:" + status + "(1:READY 2:UNREADY)");
        Intent intent = new Intent(SMInterface.getModuleInterfaceChangedAction(mContext));
        intent.putExtra(SMInterface.EXTRA_COMPONENT_NAME, componentName);
        intent.putExtra(SMInterface.EXTRA_STATUS, status);
        mContext.sendBroadcast(intent, SMInterface.getServicePermission(mContext));
    }

    private class SelfDeathRecipient implements DeathRecipient {

        private final ComponentName mComponentName;

        public SelfDeathRecipient(ComponentName componentName) {
            mComponentName = componentName;
        }

        @Override
        public void binderDied() {
            Log.i(TAG, "binderDied, componentName:" + mComponentName);
            removeModuleService(mComponentName);
        }
    }

    private static class BinderWrapper {
        protected IBinder iBinder;
        protected DeathRecipient deathRecipient;

        public BinderWrapper(IBinder iBinder, DeathRecipient deathRecipient) {
            this.iBinder = iBinder;
            this.deathRecipient = deathRecipient;
        }
    }
}
