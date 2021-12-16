package com.liux.android.service.api;

import android.content.ComponentName;
import android.os.IBinder;

interface IServiceManagerInterface {
    IBinder getService(in ComponentName componentName);
    void registerService(in ComponentName componentName, in IBinder iBinder);
    void unregisterService(in ComponentName componentName);
}