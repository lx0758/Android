package com.liux.android.sm.api;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMUtil {

    private static final Map<String, String> sMetaDataCache = new HashMap<>();
    private static final PeekServiceBR sPeekServiceBR = new PeekServiceBR();

    @Nullable
    public static String getMetaData(Context context, String key) {
        String value = sMetaDataCache.get(key);
        if (value == null) {
            try {
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                        context.getPackageName(),
                        PackageManager.GET_META_DATA
                );
                value = appInfo.metaData.getString(key);
                sMetaDataCache.put(key, value);
            } catch (Exception ignored) {}
        }
        return value;
    }

    public static void checkPermission(@NonNull Context context, @NonNull String permission) {
        if (!hasPermission(context, permission)) {
            throw new SecurityException("not granted permission " + permission);
        }
    }

    public static boolean hasPermission(@NonNull Context context, @NonNull String permission) {
        if (Binder.getCallingPid() == Process.myPid()) {
            return true;
        }
        return context.checkCallingPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Nullable
    public static ComponentName getServiceComponentName(@NonNull Context context, @Nullable String packageName, @NonNull String action) {
        Intent intent = new Intent(action);
        intent.setPackage(packageName);
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentServices(intent, 0);
        if (resolveInfoList.isEmpty()) {
            return null;
        }

        ResolveInfo resolveInfo = null;
        for (ResolveInfo info : resolveInfoList) {
            if (resolveInfo == null) {
                resolveInfo = info;
                continue;
            }
            if (info.priority > resolveInfo.priority) {
                resolveInfo = info;
            }
        }

        return new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
    }

    @Nullable
    public static IBinder peekService(Context context, ComponentName componentName) {
        Intent intent = new Intent();
        intent.setComponent(componentName);
        return sPeekServiceBR.peekService(context, intent);
    }

    private static class PeekServiceBR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
