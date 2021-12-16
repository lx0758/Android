package com.liux.android.service.api;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import androidx.annotation.Nullable;

public abstract class AbstractProxy<T extends IInterface> {

    private final IBinder.DeathRecipient mDeathRecipient = new SelfDeathRecipient();

    private final Context mContext;
    private final ComponentName mComponentName;

    private IBinder mIBinderCache;

    public AbstractProxy(Context context, String action) {
        this(context, null, action);
    }

    public AbstractProxy(Context context, String packageName, String action) {
        this(context, SMUtil.getServiceComponentName(context, packageName, action));
    }

    public AbstractProxy(Context context, ComponentName componentName) {
        mContext = context;
        mComponentName = componentName;
    }


    public boolean isAvailable() {
        return getInterface() != null;
    }

    @Nullable
    public IBinder getService() {
        if (mIBinderCache != null) {
            return mIBinderCache;
        }

        IBinder iBinder = ServiceManager.getInstance(mContext).getService(mComponentName);
        if (iBinder != null) {
            try {
                iBinder.linkToDeath(mDeathRecipient, 0);
                mIBinderCache = iBinder;
            } catch (RemoteException e) {
                return null;
            }
        }

        return mIBinderCache;
    }

    @Nullable
    public abstract T getInterface();

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
