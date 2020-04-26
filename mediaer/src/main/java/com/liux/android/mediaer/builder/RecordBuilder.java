package com.liux.android.mediaer.builder;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.action.IntentAction;
import com.liux.android.mediaer.MediaerUtil;
import com.liux.android.mediaer.inject.IntentTask;
import com.liux.android.mediaer.listener.OnCancelListener;
import com.liux.android.mediaer.listener.OnFailureListener;
import com.liux.android.mediaer.listener.OnRecordListener;

public class RecordBuilder extends Builder<RecordBuilder, IntentAction<RecordBuilder, Uri>> {

    public Uri outUri;
    public int duration, size, quality;

    public OnRecordListener onRecordListener;

    public RecordBuilder(IntentAction<RecordBuilder, Uri> action, FragmentActivity target) {
        super(action, target);
    }

    public RecordBuilder(IntentAction<RecordBuilder, Uri> action, FragmentActivity target, Uri outUri) {
        super(action, target);
        this.outUri = outUri;
    }

    public RecordBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public RecordBuilder size(int size) {
        this.size = size;
        return this;
    }

    public RecordBuilder quality(int quality) {
        if (quality < 0) quality = 0;
        if (quality > 1) quality = 1;
        this.quality = quality;
        return this;
    }

    public RecordBuilder listener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
        return this;
    }

    @Override
    public RecordBuilder listener(OnCancelListener onCancelListener) {
        return super.listener(onCancelListener);
    }

    @Override
    String[] onDeclarePermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };
    }

    @Override
    OnFailureListener onDeclareFailureListener() {
        return onRecordListener;
    }

    @Override
    void onStart() {
        if (outUri == null) outUri = MediaerUtil.getProviderUri(target, MediaerUtil.getCacheTempFile(target));
        executeTask(new IntentTask() {
            @Override
            public void onExecute(Fragment fragment, int requestCode) {
                try {
                    action.onStart(target, fragment, RecordBuilder.this, requestCode);
                } catch (MediaerException e) {
                    onFailure(e);
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                try {
                    Uri result = action.onFinish(target, RecordBuilder.this, resultCode, data);
                    if (onRecordListener != null) onRecordListener.onSucceed(result);
                } catch (MediaerException e) {
                    onFailure(e);
                }
            }
        });
    }
}
