package com.liux.android.sm.api;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.NoSuchElementException;

public abstract class ModuleInterface<T extends IInterface> {
    protected final String TAG = getClass().getSimpleName() + "-MI";

    private final IBinder.DeathRecipient mDeathRecipient = new SelfDeathRecipient();

    private final Context mContext;
    private final ComponentName mComponentName;

    private T mInterfaceCache;

    public ModuleInterface(Context context, String action) {
        this(context, null, action);
    }

    public ModuleInterface(Context context, String packageName, String action) {
        this(context, SMUtil.getServiceComponentName(context, packageName, action));
    }

    public ModuleInterface(Context context, ComponentName componentName) {
        mContext = context.getApplicationContext();
        mComponentName = componentName;
    }

    public boolean isAvailable() {
        return getInterface() != null;
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    @Nullable
    public T getInterface() {
        if (mInterfaceCache == null) {
            IBinder iBinder = SMInterface.getInstance(mContext).getService(mComponentName);
            if (iBinder != null) {
                T tInterface = onBinderToInterface(iBinder);
                if (tInterface != null) {
                    try {
                        tInterface.asBinder().linkToDeath(mDeathRecipient, 0);
                    } catch (RemoteException e) {
                        return null;
                    }
                }
                mInterfaceCache = tInterface;
            }
        }
        Log.d(TAG, "getInterface, result:" + (mInterfaceCache != null ? "not null" : "null"));
        return mInterfaceCache;
    }

    @Nullable
    public abstract T onBinderToInterface(IBinder iBinder);

    private class SelfDeathRecipient implements IBinder.DeathRecipient {
        @Override
        public void binderDied() {
            Log.w(TAG, "SelfDeathRecipient -> binderDied");
            if (mInterfaceCache != null) {
                try {
                    mInterfaceCache.asBinder().unlinkToDeath(mDeathRecipient, 0);
                } catch (NoSuchElementException ignored) {}
                mInterfaceCache = null;
            }
        }
    }
}
