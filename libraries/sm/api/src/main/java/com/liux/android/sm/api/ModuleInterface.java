package com.liux.android.sm.api;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class ModuleInterface<T extends IInterface> {
    protected final String TAG = getClass().getSimpleName() + "-MI";

    private final IBinder.DeathRecipient mDeathRecipient = new SelfDeathRecipient();

    private final Context mContext;
    private final ComponentName mComponentName;

    private T mModuleInterfaceCache;

    public ModuleInterface(Context context, String action) {
        this(context, null, action);
    }

    public ModuleInterface(Context context, String packageName, String action) {
        this(
                context,
                Objects.requireNonNull(SMUtil.getServiceComponentName(context, packageName, action))
        );
    }

    public ModuleInterface(Context context, ComponentName componentName) {
        mContext = context.getApplicationContext();
        String className = componentName.getClassName();
        if (className.charAt(0) == '.') {
            mComponentName = new ComponentName(componentName.getPackageName(), componentName.getPackageName() + className);
        } else {
            mComponentName = componentName;
        }
    }

    public boolean isModuleAvailable() {
        return getModuleInterface() != null;
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    @Nullable
    public T getModuleInterface() {
        if (mModuleInterfaceCache == null) {
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
                mModuleInterfaceCache = tInterface;
            }
        }
        Log.d(TAG, "getModuleInterface, result:" + (mModuleInterfaceCache != null ? "not null" : "null"));
        return mModuleInterfaceCache;
    }

    @Nullable
    public abstract T onBinderToInterface(IBinder iBinder);

    private class SelfDeathRecipient implements IBinder.DeathRecipient {
        @Override
        public void binderDied() {
            Log.w(TAG, "SelfDeathRecipient -> binderDied");
            if (mModuleInterfaceCache != null) {
                try {
                    mModuleInterfaceCache.asBinder().unlinkToDeath(mDeathRecipient, 0);
                } catch (NoSuchElementException ignored) {}
                mModuleInterfaceCache = null;
            }
        }
    }
}
