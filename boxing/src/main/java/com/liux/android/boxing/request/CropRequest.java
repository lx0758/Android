package com.liux.android.boxing.request;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.liux.android.boxing.BoxingFragment;
import com.liux.android.boxing.BoxingUcrop;
import com.liux.android.boxing.BoxingUtil;
import com.liux.android.boxing.OnCropListener;
import com.liux.android.boxing.Request;
import com.liux.android.boxing.Task;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class CropRequest extends Request {

    Uri inUri, outUri;
    OnCropListener onCropListener;
    int maxWidth, maxHeight;
    float aspectRatioX, aspectRatioY;

    public CropRequest(Activity target, Uri inUri) {
        super(target);
        this.inUri = inUri;
    }

    public CropRequest outUri(Uri outUri) {
        this.outUri = outUri;
        return this;
    }

    public CropRequest maxSize(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        return this;
    }

    public CropRequest aspectRatio(float aspectRatioX, float aspectRatioY) {
        this.aspectRatioX = aspectRatioX;
        this.aspectRatioY = aspectRatioY;
        return this;
    }

    public CropRequest listener(OnCropListener onCropListener) {
        this.onCropListener = onCropListener;
        return this;
    }

    @Override
    public void start() {
        if (outUri == null) outUri = Uri.fromFile(new File(BoxingUtil.getCacheDir(target), String.valueOf(System.currentTimeMillis())));
        BoxingFragment boxingFragment = BoxingUtil.getPermissionFragment(target);
        boxingFragment.executeTask(new Task() {
            @Override
            public void onMainThreadExecute(int requestCode, BoxingFragment boxingFragment) {
                UCrop.Options options = BoxingUcrop.getOptions(target, maxWidth, maxHeight, aspectRatioX, aspectRatioY);
                UCrop.of(inUri, outUri)
                        .withOptions(options)
                        .start(target, boxingFragment, requestCode);
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (data == null) return;

                Throwable throwable = UCrop.getError(data);
                if (throwable != null) {
                    callFailure();
                    return;
                }

                callSucceed(UCrop.getOutput(data));
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            }
        });
    }

    private void callSucceed(Uri output) {
        if (onCropListener != null) onCropListener.onSucceed(output);
    }

    private void callFailure() {
        if (onCropListener != null) onCropListener.onFailure();
    }
}
