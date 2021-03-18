package com.liux.android.permission.install;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.liux.android.permission.Continue;
import com.liux.android.permission.PermissionFragment;
import com.liux.android.permission.PermissionUtil;
import com.liux.android.permission.Request;
import com.liux.android.permission.Task;

public class InstallRequest extends Request<InstallRequest> {

    private OnInstallPermissionListener onInstallPermissionListener;

    public InstallRequest(Activity target) {
        super(target);
    }

    public InstallRequest listener(OnInstallPermissionListener listener) {
        this.onInstallPermissionListener = listener;
        return this;
    }

    @Override
    public void request() {
        if (onInstallPermissionListener == null) {
            throw new NullPointerException("listener(OnInstallPermissionListener) cannot is null");
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || target.getPackageManager().canRequestPackageInstalls()) {
            onInstallPermissionListener.onSucceed();
            return;
        }

        if (onContinueListener != null) {
            onContinueListener.onContinue(new Continue() {
                @Override
                public void onContinue() {
                    onRealRequestCall();
                }

                @Override
                public void onCancel() {
                    onInstallPermissionListener.onFailure();
                }
            });
        } else {
            onRealRequestCall();
        }
    }

    /**
     * 一共使用两种方案进行申请
     * 先使用类似 Android M 类似的权限申请方式
     * 如果失败,则跳转系统 Activity 设置得到最后的结果
     */
    private void onRealRequestCall() {
        final PermissionFragment permissionFragment = PermissionUtil.getPermissionFragment(target);
        permissionFragment.executeTask(new Task() {
            @Override
            @TargetApi(Build.VERSION_CODES.O)
            public void onMainThreadExecute(int requestCode, PermissionFragment permissionFragment) {
                permissionFragment.requestPermissions(new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, requestCode);
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {

            }

            @Override
            @TargetApi(Build.VERSION_CODES.O)
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (target.getPackageManager().canRequestPackageInstalls()) {
                    onInstallPermissionListener.onSucceed();
                } else {
                    onRealRequestCallActivity(permissionFragment);
                }
            }
        });
    }

    private void onRealRequestCallActivity(PermissionFragment permissionFragment) {
        permissionFragment.executeTask(new Task() {
            @Override
            @TargetApi(Build.VERSION_CODES.O)
            public void onMainThreadExecute(int requestCode, PermissionFragment permissionFragment) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent.setData(Uri.fromParts("package", permissionFragment.getContext().getPackageName(), null));
                permissionFragment.startActivityForResult(intent, requestCode);
            }

            @Override
            @TargetApi(Build.VERSION_CODES.O)
            public void onActivityResult(int resultCode, Intent data) {
                if (target.getPackageManager().canRequestPackageInstalls()) {
                    onInstallPermissionListener.onSucceed();
                } else {
                    onInstallPermissionListener.onFailure();
                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            }
        });
    }
}
