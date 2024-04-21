package com.liux.android.permission.floats;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

import com.liux.android.permission.PermissionFragment;

class OppoUtil {

    protected static boolean isOppoRom() {
        //https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        return Build.MANUFACTURER.toUpperCase().contains("OPPO");
    }

    protected static void gotoPermissionApply(int requestCode, PermissionFragment fragment) throws Exception {
        //merge requestRuntime from https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");//悬浮窗管理页面
        intent.setComponent(comp);
        fragment.startActivityForResult(intent, requestCode);
    }
}
