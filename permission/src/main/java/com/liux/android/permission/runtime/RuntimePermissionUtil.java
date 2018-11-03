package com.liux.android.permission.runtime;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.PermissionChecker;

public class RuntimePermissionUtil {

    private static final String MARK = Build.MANUFACTURER.toLowerCase();

    /**
     * 检查是否拥有某些权限
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context == null) return false;
        if (permissions == null) return true;

        for (String permission: permissions) {
            if (PermissionChecker.checkCallingOrSelfPermission(context, permission)
                    != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取跳转到应用权限设置页面的 Intent
     *
     * @param context 上下文对象
     */
    public static Intent getPermissionSettingIntent(Context context) {
        Intent intent;
        if (MARK.contains("huawei")) {
            intent = getHuaweiIntent(context);
        } else if (MARK.contains("xiaomi")) {
            intent = getXiaomiIntent(context);
        } else if (MARK.contains("oppo")) {
            intent = getOppoIntent(context);
        } else if (MARK.contains("vivo")) {
            intent = getVivoIntent(context);
        } else if (MARK.contains("meizu")) {
            intent = getMeizuIntent(context);
        } else {
            intent = getGoogleIntent(context);
        }
        if (hasIntent(context, intent)) return intent;

        return getGoogleIntent(context);
    }

    private static Intent getHuaweiIntent(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
        if (hasIntent(context, intent)) return intent;
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity"));
        if (hasIntent(context, intent)) return intent;
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity"));
        return intent;
    }

    private static Intent getXiaomiIntent(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (hasIntent(context, intent)) return intent;

        intent.setPackage("com.miui.securitycenter");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.requestRuntime.AppPermissionsEditorActivity");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.requestRuntime.PermissionsEditorActivity");
        return intent;
    }

    private static Intent getOppoIntent(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        return intent;
    }

    private static Intent getVivoIntent(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");
        intent.putExtra("packagename", context.getPackageName());
        if (hasIntent(context, intent)) return intent;

        intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
        return intent;
    }

    private static Intent getMeizuIntent(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    private static Intent getGoogleIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    private static boolean hasIntent(Context context, Intent intent) {
        return !context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }
}
