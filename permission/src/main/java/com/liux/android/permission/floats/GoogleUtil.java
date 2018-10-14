package com.liux.android.permission.floats;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.liux.android.permission.PermissionFragment;

import java.lang.reflect.Field;

class GoogleUtil {

    protected static void gotoPermissionApply(int requestCode, PermissionFragment fragment) throws Exception {
        Class clazz = Settings.class;
        Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
        Intent intent = new Intent(field.get(null).toString());
        intent.setData(Uri.parse("package:" + fragment.getActivity().getPackageName()));
        fragment.startActivityForResult(intent, requestCode);
    }
}
