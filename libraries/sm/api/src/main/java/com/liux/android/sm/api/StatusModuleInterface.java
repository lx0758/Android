package com.liux.android.sm.api;

import android.content.ComponentName;
import android.content.Context;
import android.os.IInterface;

public abstract class StatusModuleInterface<T extends IInterface> extends ModuleInterface<T> implements SMInterface.StatusListener {

    private final Context mContext;

    public StatusModuleInterface(Context context, String action) {
        super(context, action);
        mContext = context;
    }

    public StatusModuleInterface(Context context, String packageName, String action) {
        super(context, packageName, action);
        mContext = context;
    }

    public StatusModuleInterface(Context context, ComponentName componentName) {
        super(context, componentName);
        mContext = context;
    }

    public void registerStatusListener() {
        SMInterface.getInstance(mContext).registerStatusListener(this);
    }

    public void unregisterStatusListener() {
        SMInterface.getInstance(mContext).unregisterStatusListener(this);
    }
}
