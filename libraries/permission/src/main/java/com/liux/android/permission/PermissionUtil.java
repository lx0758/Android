package com.liux.android.permission;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.liux.android.permission.floats.FloatPermissionUtil;
import com.liux.android.permission.runtime.RuntimePermissionUtil;

public class PermissionUtil {

    /**
     * 获取/创建并注入 权限申请需要的 Fragmrnt
     * @return
     */
    public static PermissionFragment getPermissionFragment(Activity activity) {
        PermissionFragment fragment;
        fragment = (PermissionFragment) activity.getFragmentManager().findFragmentByTag(Authorizer.TAG);
        if (fragment != null) return fragment;

        fragment = new PermissionFragment();
        FragmentManager manager = activity.getFragmentManager();
        manager
                .beginTransaction()
                .add(fragment, Authorizer.TAG)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();
        return fragment;
    }

    /**
     * 移除注入的 PermissionFragment
     * @param fragment
     */
    public static void removePermissionFragment(PermissionFragment fragment) {
        Activity target = fragment.getActivity();
        FragmentManager manager = target.getFragmentManager();
        manager.beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();
    }

    /**
     * 判断是否具有M权限
     * @param context
     * @return
     */
    public static boolean hasMPermission(Context context, String... permissions) {
        return RuntimePermissionUtil.hasPermissions(context, permissions);
    }

    /**
     * 判断是否具有悬浮窗权限
     * @param context
     * @return
     */
    public static boolean hasFloatsPermission(Context context) {
        return FloatPermissionUtil.hasPermission(context);
    }
}
