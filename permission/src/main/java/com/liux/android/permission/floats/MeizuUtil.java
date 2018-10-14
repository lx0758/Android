package com.liux.android.permission.floats;

import android.content.Intent;
import android.text.TextUtils;

import com.liux.android.permission.PermissionFragment;

class MeizuUtil {

    protected static boolean isMeizuRom() {
        //return Build.MANUFACTURER.contains("Meizu");
        String property  = FloatPermissionUtil.getSystemProperty("ro.build.display.id");
        if (TextUtils.isEmpty(property)) return false;
        return property.toLowerCase().contains("flyme");
    }

    protected static void gotoPermissionApply(int requestCode, PermissionFragment fragment) throws Exception {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        // remove this line code for fix flyme_6.3
        //intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", fragment.getActivity().getPackageName());
        fragment.startActivityForResult(intent, requestCode);
    }
}
