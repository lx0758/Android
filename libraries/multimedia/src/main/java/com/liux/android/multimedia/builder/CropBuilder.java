package com.liux.android.multimedia.builder;

import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.MultimediaUtil;
import com.liux.android.multimedia.action.IntentAction;
import com.liux.android.multimedia.inject.IntentTask;
import com.liux.android.multimedia.listener.OnCancelListener;
import com.liux.android.multimedia.listener.OnCropListener;
import com.liux.android.multimedia.listener.OnFailureListener;

public class CropBuilder extends Builder<CropBuilder, IntentAction<CropBuilder, Uri>> {

    public Uri inUri, outUri;
    public int maxWidth, maxHeight;
    public float aspectRatioX, aspectRatioY;

    public OnCropListener onCropListener;

    public CropBuilder(IntentAction<CropBuilder, Uri> action, FragmentActivity target, Uri inUri) {
        super(action, target);
        this.inUri = inUri;
    }

    public CropBuilder outUri(Uri outUri) {
        this.outUri = outUri;
        return this;
    }

    public CropBuilder maxSize(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        return this;
    }

    public CropBuilder aspectRatio(float aspectRatioX, float aspectRatioY) {
        this.aspectRatioX = aspectRatioX;
        this.aspectRatioY = aspectRatioY;
        return this;
    }

    @Override
    public CropBuilder listener(OnCancelListener onCancelListener) {
        return super.listener(onCancelListener);
    }

    public CropBuilder listener(OnCropListener onCropListener) {
        this.onCropListener = onCropListener;
        return this;
    }

    @Override
    String[] onDeclarePermissions() {
        return null;
    }

    @Override
    OnFailureListener onDeclareFailureListener() {
        return onCropListener;
    }

    @Override
    void onStart() {
        if (outUri == null) outUri = MultimediaUtil.getProviderUri(target, MultimediaUtil.getCacheTempFile(target));
        executeTask(new IntentTask() {
            @Override
            public void onExecute(Fragment fragment, int requestCode) {
                try {
                    action.onStart(target, fragment, CropBuilder.this, requestCode);
                } catch (MultimediaException e) {
                    onFailure(e);
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                try {
                    Uri result = action.onFinish(target, CropBuilder.this, resultCode, data);
                    if (onCropListener != null) onCropListener.onSucceed(result);
                } catch (MultimediaException e) {
                    onFailure(e);
                }
            }
        });
    }

    public interface Factory {

        IntentAction<CropBuilder, Uri> createAction();
    }
}
