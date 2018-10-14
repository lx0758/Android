package com.liux.android.permission;

import android.app.Activity;
import android.app.Fragment;

/**
 * Created by Liux on 2017/8/7.
 */

public class PermissionTool {
    public static final String TAG = "PermissionTool";

    public static Prepare with(Activity activity) {
        return new Prepare(activity);
    }

    public static Prepare with(Fragment fragment) {
        return new Prepare(fragment);
    }

    public static Prepare with(android.support.v4.app.Fragment fragment) {
        return new Prepare(fragment);
    }
}
