package com.liux.android.permission.floats;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.liux.android.permission.Continue;
import com.liux.android.permission.PermissionFragment;
import com.liux.android.permission.PermissionUtil;
import com.liux.android.permission.Request;
import com.liux.android.permission.Task;

public class FloatRequest extends Request<FloatRequest> {

    private OnFloatPermissionListener onFloatPermissionListener;

    public FloatRequest(Activity target) {
        super(target);
    }

    public FloatRequest listener(OnFloatPermissionListener listener) {
        this.onFloatPermissionListener = listener;
        return this;
    }

    @Override
    public void request() {
        if (onFloatPermissionListener == null) {
            throw new NullPointerException("listener(OnFloatPermissionListener) cannot is null");
        }

        if (FloatPermissionUtil.hasPermission(target)) {
            onFloatPermissionListener.onSucceed();
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
                    onFloatPermissionListener.onFailure();
                }
            });
        } else {
            onRealRequestCall();
        }
    }

    private void onRealRequestCall() {
        PermissionFragment permissionFragment = PermissionUtil.getPermissionFragment(target);
        permissionFragment.executeTask(new Task() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onMainThreadExecute(int requestCode, PermissionFragment permissionFragment) {
                try {
                    FloatPermissionUtil.gotoPermissionApply(requestCode, permissionFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFloatPermissionListener.onFailure();
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (FloatPermissionUtil.hasPermissionForResult(target)) {
                    onFloatPermissionListener.onSucceed();
                } else {
                    onFloatPermissionListener.onFailure();
                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            }
        });
    }
}
