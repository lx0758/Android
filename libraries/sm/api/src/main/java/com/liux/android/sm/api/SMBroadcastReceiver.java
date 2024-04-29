package com.liux.android.sm.api;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class SMBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action == null) return;

        if (SMInterface.getServiceReadyAction(context).equals(action)) {
            onServiceManagerReady(context, intent);
            return;
        }
        if (SMInterface.getModuleInterfaceChangedAction(context).equals(action)) {
            ComponentName componentName = intent.getParcelableExtra(SMInterface.EXTRA_COMPONENT_NAME);
            int status = intent.getIntExtra(SMInterface.EXTRA_STATUS, 0);
            onModuleInterfaceChanged(context, intent, componentName, status);
            return;
        }
    }

    public void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SMInterface.getServiceReadyAction(context));
        intentFilter.addAction(SMInterface.getModuleInterfaceChangedAction(context));
        context.registerReceiver(
                this,
                intentFilter,
                SMInterface.getServicePermission(context),
                null
        );
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(this);
    }

    public void onServiceManagerReady(Context context, Intent intent) {

    }

    public void onModuleInterfaceChanged(Context context, Intent intent, ComponentName componentName, int status) {

    }
}
