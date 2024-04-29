package com.liux.android.sm.api;

import android.content.ComponentName;
import android.os.IBinder;

interface ISMInterface {
    IBinder getService(in ComponentName componentName);
    void registerService(in ComponentName componentName, in IBinder iBinder);
    void unregisterService(in ComponentName componentName);
}