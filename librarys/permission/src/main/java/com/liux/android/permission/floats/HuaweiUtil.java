package com.liux.android.permission.floats;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

import com.liux.android.permission.PermissionFragment;

class HuaweiUtil {

    protected static boolean isHuaweiRom() {
        return Build.MANUFACTURER.toUpperCase().contains("HUAWEI");
    }

    protected static void gotoPermissionApply(int requestCode, PermissionFragment fragment) throws Exception {
        try {
            Intent intent = new Intent();
            //ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            //ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.SingleAppActivity");//华为权限管理，跳转到指定app的权限管理位置需要华为接口权限，未解决
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            if (getEmuiVersion() == 3.1) {
                //emui 3.1 的适配
                fragment.startActivityForResult(intent, requestCode);
            } else {
                //emui 3.0 的适配
                comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");//悬浮窗管理页面
                intent.setComponent(comp);
                fragment.startActivityForResult(intent, requestCode);
            }
        } catch (SecurityException e) {
            Intent intent = new Intent();
            //ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理，跳转到本app的权限管理页面,这个需要华为接口权限，未解决
            //ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            fragment.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            /**
             * 手机管家版本较低 HUAWEI SC-UL10
             */
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.Android.settings", "com.android.settings.permission.TabItem");//权限管理页面 android4.4
            //ComponentName comp = new ComponentName("com.android.settings","com.android.settings.permission.single_app_activity");//此处可跳转到指定app对应的权限管理页面，但是需要相关权限，未解决
            intent.setComponent(comp);
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取 emui 版本号
     * @return
     */
    private static double getEmuiVersion() {
        try {
            String emuiVersion = FloatPermissionUtil.getSystemProperty("ro.build.version.emui");
            String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            return Double.parseDouble(version);
        } catch (Exception ignore) {

        }
        return 4.0;
    }
}
