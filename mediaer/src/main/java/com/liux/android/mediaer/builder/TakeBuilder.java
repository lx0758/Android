package com.liux.android.mediaer.builder;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.mediaer.Mediaer;
import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.action.IntentAction;
import com.liux.android.mediaer.inject.IntentTask;
import com.liux.android.mediaer.MediaerUtil;
import com.liux.android.mediaer.listener.OnCancelListener;
import com.liux.android.mediaer.listener.OnCropListener;
import com.liux.android.mediaer.listener.OnFailureListener;
import com.liux.android.mediaer.listener.OnTakeListener;

public class TakeBuilder extends Builder<TakeBuilder, IntentAction<TakeBuilder, Uri>> {

    public Uri outUri;
    public boolean useCrop;

    public OnTakeListener onTakeListener;

    public TakeBuilder(IntentAction<TakeBuilder, Uri> action, FragmentActivity target) {
        super(action, target);
    }

    public TakeBuilder(IntentAction<TakeBuilder, Uri> action, FragmentActivity target, Uri outUri) {
        super(action, target);
        this.outUri = outUri;
    }

    public TakeBuilder useCrop(boolean useCrop) {
        this.useCrop = useCrop;
        return this;
    }

    public TakeBuilder listener(OnTakeListener onTakeListener) {
        this.onTakeListener = onTakeListener;
        return this;
    }

    @Override
    public TakeBuilder listener(OnCancelListener onCancelListener) {
        return super.listener(onCancelListener);
    }

    @Override
    String[] onDeclarePermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
    }

    @Override
    OnFailureListener onDeclareFailureListener() {
        return onTakeListener;
    }

    @Override
    void onStart() {
        if (outUri == null) outUri = MediaerUtil.getProviderUri(target, MediaerUtil.getCacheTempFile(target));
        executeTask(new IntentTask() {
            @Override
            public void onExecute(Fragment fragment, int requestCode) {
                try {
                    action.onStart(target, fragment, TakeBuilder.this, requestCode);
                } catch (MediaerException e) {
                    onFailure(e);
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                try {
                    Uri result = action.onFinish(target, TakeBuilder.this, resultCode, data);
                    if (useCrop) {
                        Mediaer.with(target)
                                .crop(result)
                                .listener(onCancelListener)
                                .listener(new OnCropListener() {
                                    @Override
                                    public void onSucceed(Uri output) {
                                        if (onTakeListener != null) onTakeListener.onSucceed(output);
                                    }

                                    @Override
                                    public void onFailure(MediaerException e) {
                                        TakeBuilder.this.onFailure(e);
                                    }
                                })
                                .start();
                        return;
                    }
                    if (onTakeListener != null) onTakeListener.onSucceed(result);
                } catch (MediaerException e) {
                    onFailure(e);
                }
            }
        });
    }
}
