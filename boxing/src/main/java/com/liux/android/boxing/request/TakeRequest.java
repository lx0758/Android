package com.liux.android.boxing.request;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;

import com.liux.android.boxing.BoxingFragment;
import com.liux.android.boxing.BoxingUtil;
import com.liux.android.boxing.OnTakeListener;
import com.liux.android.boxing.Request;
import com.liux.android.boxing.Task;

import java.io.File;

public class TakeRequest extends Request {

    Uri outUri;
    String authority;
    OnTakeListener onTakeListener;

    public TakeRequest(Activity target, Uri outUri) {
        super(target);
        this.outUri = outUri;
    }

    public TakeRequest(Activity target, String authority) {
        super(target);
        this.authority = authority;
    }

    public TakeRequest listener(OnTakeListener onTakeListener) {
        this.onTakeListener = onTakeListener;
        return this;
    }

    @Override
    public void start() {
        if (PermissionChecker.checkCallingOrSelfPermission(target, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            callRequestPermission();
        } else {
            callTake();
        }
    }

    /**
     * 申请权限
     */
    private void callRequestPermission() {
        BoxingFragment boxingFragment = BoxingUtil.getPermissionFragment(target);
        boxingFragment.executeTask(new Task() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onMainThreadExecute(int requestCode, BoxingFragment boxingFragment) {
                boxingFragment.requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        requestCode
                );
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {

            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (PermissionChecker.checkCallingOrSelfPermission(target, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
                    callTake();
                    return;
                }
                callFailure(OnTakeListener.ERROR_PERMISSION);
            }
        });
    }

    /**
     * 调用相机拍照
     */
    private void callTake() {
        if (outUri == null) {
            File file = BoxingUtil.getCacheTempFile(target);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                outUri = Uri.fromFile(file);
            } else {
                outUri = FileProvider.getUriForFile(target, authority, file);
            }
        }
        BoxingFragment boxingFragment = BoxingUtil.getPermissionFragment(target);
        boxingFragment.executeTask(new Task() {
            @Override
            public void onMainThreadExecute(int requestCode, BoxingFragment boxingFragment) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
                    intent.putExtra("return-data", false);
                    boxingFragment.startActivityForResult(intent, requestCode);
                } catch (Exception e) {
                    callFailure(OnTakeListener.ERROR_INTENT);
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (Activity.RESULT_OK != resultCode) return;
                callSucceed();
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            }
        });
    }

    /**
     * 处理成功
     */
    private void callSucceed() {
        if (onTakeListener != null) onTakeListener.onSucceed(outUri);
    }

    /**
     * 处理失败
     * @param type
     */
    private void callFailure(int type) {
        if (onTakeListener != null) onTakeListener.onFailure(type);
    }
}
