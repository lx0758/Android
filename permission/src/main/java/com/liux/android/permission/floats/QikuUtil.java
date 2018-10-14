package com.liux.android.permission.floats;

import android.content.Intent;
import android.os.Build;

import com.liux.android.permission.PermissionFragment;

class QikuUtil {

    protected static boolean isQikuRom() {
        //fix issue https://github.com/zhaozepeng/FloatWindowPermission/issues/9
        return Build.MANUFACTURER.toUpperCase().contains("QIKU") || Build.MANUFACTURER.contains("360");
    }

    protected static void gotoPermissionApply(int requestCode, PermissionFragment fragment) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            fragment.startActivityForResult(intent, requestCode);
        }
    }
}
