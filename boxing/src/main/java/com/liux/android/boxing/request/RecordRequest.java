package com.liux.android.boxing.request;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;

import com.liux.android.boxing.BoxingFragment;
import com.liux.android.boxing.BoxingUtil;
import com.liux.android.boxing.OnCancelListener;
import com.liux.android.boxing.OnRecordListener;
import com.liux.android.boxing.OnTakeListener;
import com.liux.android.boxing.Request;
import com.liux.android.boxing.Task;

import java.io.File;

public class RecordRequest extends Request<RecordRequest> {

    Uri outUri;
    String authority;
    int duration, size, quality;
    OnRecordListener onRecordListener;

    public RecordRequest(Activity target, Uri outUri) {
        super(target);
        this.outUri = outUri;
    }

    public RecordRequest(Activity target, String authority) {
        super(target);
        this.authority = authority;
    }

    public RecordRequest duration(int duration) {
        this.duration = duration;
        return this;
    }

    public RecordRequest size(int size) {
        this.size = size;
        return this;
    }

    public RecordRequest quality(int quality) {
        if (quality < 0) quality = 0;
        if (quality > 1) quality = 1;
        this.quality = quality;
        return this;
    }

    public RecordRequest listener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
        return this;
    }

    @Override
    public RecordRequest listener(OnCancelListener onCancelListener) {
        return super.listener(onCancelListener);
    }

    @Override
    public void start() {
        if (PermissionChecker.checkCallingOrSelfPermission(target, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED ||
                PermissionChecker.checkCallingOrSelfPermission(target, Manifest.permission.RECORD_AUDIO) != PermissionChecker.PERMISSION_GRANTED) {
            callRequestPermission();
        } else {
            callRecord();
        }
    }

    private void callRequestPermission() {
        BoxingFragment boxingFragment = BoxingUtil.getPermissionFragment(target);
        boxingFragment.executeTask(new Task() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onMainThreadExecute(int requestCode, BoxingFragment boxingFragment) {
                boxingFragment.requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                        requestCode
                );
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {

            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (PermissionChecker.checkCallingOrSelfPermission(target, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED &&
                        PermissionChecker.checkCallingOrSelfPermission(target, Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED) {
                    callRecord();
                    return;
                }
                callFailure(OnTakeListener.ERROR_PERMISSION);
            }
        });
    }

    private void callRecord() {
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
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);
                    if (duration > 0) intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, duration);
                    if (size > 0) intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, size);
                    boxingFragment.startActivityForResult(intent, requestCode);
                } catch (Exception e) {
                    callFailure(OnRecordListener.ERROR_INTENT);
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode != Activity.RESULT_OK) {
                    if (onCancelListener != null) onCancelListener.onCancel();
                    return;
                }
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
        if (onRecordListener != null) onRecordListener.onSucceed(outUri);
    }

    /**
     * 处理失败
     * @param type
     */
    private void callFailure(int type) {
        if (onRecordListener != null) onRecordListener.onFailure(type);
    }
}
