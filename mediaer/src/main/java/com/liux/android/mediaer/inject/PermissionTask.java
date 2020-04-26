package com.liux.android.mediaer.inject;

import android.content.Context;

import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

public abstract class PermissionTask implements Task {

    private String[] permissions;

    public PermissionTask(String[] permissions) {
        this.permissions = permissions;
    }

    public void onExecute(Fragment fragment, int requestCode) {
        fragment.requestPermissions(permissions, requestCode);
    }

    public final void onRequestPermissionsResult(Context context, String[] permissions, int[] grantResults) {
        for (String permission : permissions) {
            if (PermissionChecker.checkCallingOrSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
                onReject();
                return;
            }
        }
        onGrant();
    }

    protected abstract void onGrant();

    protected abstract void onReject();
}
