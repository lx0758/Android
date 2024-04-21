package com.liux.android.service.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SMUtil {

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
        if (resolveInfoList == null || resolveInfoList.isEmpty()) {
            return null;
        }

        ResolveInfo resolveInfo = null;
        for (ResolveInfo info : resolveInfoList) {
            if (resolveInfo == null) {
                resolveInfo = info;
                continue;
            }
            if (resolveInfo.priority > info.priority) {
                resolveInfo = info;
            }
        }

        return new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
    }
}
