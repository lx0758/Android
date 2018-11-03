package com.liux.android.permission;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface Task {

    /**
     * 切换到主线程回调
     * @param requestCode
     * @param permissionFragment
     */
    void onMainThreadExecute(int requestCode, PermissionFragment permissionFragment);

    /**
     * 启动页面的毁掉
     * @param resultCode
     * @param data
     */
    void onActivityResult(int resultCode, Intent data);

    /**
     * 权限申请的回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
