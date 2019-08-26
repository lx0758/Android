package com.liux.android.boxing;

import android.content.Intent;

import androidx.annotation.NonNull;

public interface Task {

    /**
     * 切换到主线程回调
     * @param requestCode
     * @param boxingFragment
     */
    void onMainThreadExecute(int requestCode, BoxingFragment boxingFragment);

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
