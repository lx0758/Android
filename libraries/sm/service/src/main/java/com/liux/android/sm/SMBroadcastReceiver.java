package com.liux.android.sm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SMBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SMBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive, action:" + (intent != null ? intent.getAction() : null));
    }
}
