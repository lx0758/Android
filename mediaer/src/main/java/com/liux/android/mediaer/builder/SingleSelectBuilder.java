package com.liux.android.mediaer.builder;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.mediaer.Mediaer;
import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.action.IntentAction;
import com.liux.android.mediaer.inject.IntentTask;
import com.liux.android.mediaer.listener.OnCancelListener;
import com.liux.android.mediaer.listener.OnCropListener;
import com.liux.android.mediaer.listener.OnFailureListener;
import com.liux.android.mediaer.listener.OnSingleSelectListener;

public class SingleSelectBuilder extends Builder<SingleSelectBuilder, IntentAction<SingleSelectBuilder, Uri>> {

    public boolean useCrop = false;
    public OnSingleSelectListener onSingleSelectListener;

    public SingleSelectBuilder(IntentAction<SingleSelectBuilder, Uri> action, FragmentActivity target) {
        super(action, target);
    }

    public SingleSelectBuilder useCrop(boolean useCrop) {
        this.useCrop = useCrop;
        return this;
    }

    public SingleSelectBuilder listener(OnSingleSelectListener onSingleSelectListener) {
        this.onSingleSelectListener = onSingleSelectListener;
        return this;
    }

    @Override
    public SingleSelectBuilder listener(OnCancelListener onCancelListener) {
        return super.listener(onCancelListener);
    }

    @Override
    String[] onDeclarePermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) return null;
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    @Override
    OnFailureListener onDeclareFailureListener() {
        return onSingleSelectListener;
    }

    @Override
    void onStart() {
        executeTask(new IntentTask() {
            @Override
            public void onExecute(Fragment fragment, int requestCode) {
                try {
                    action.onStart(target, fragment, SingleSelectBuilder.this, requestCode);
                } catch (MediaerException e) {
                    onFailure(e);
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                try {
                    Uri result = action.onFinish(target, SingleSelectBuilder.this, resultCode, data);
                    if (useCrop) {
                        Mediaer.with(target)
                                .crop(result)
                                .listener(onCancelListener)
                                .listener(new OnCropListener() {
                                    @Override
                                    public void onSucceed(Uri output) {
                                        if (onSingleSelectListener != null) onSingleSelectListener.onSingleSelect(output);
                                    }

                                    @Override
                                    public void onFailure(MediaerException e) {
                                        SingleSelectBuilder.this.onFailure(e);
                                    }
                                })
                                .start();
                        return;
                    }
                    if (onSingleSelectListener != null) onSingleSelectListener.onSingleSelect(result);
                } catch (MediaerException e) {
                    onFailure(e);
                }
            }
        });
    }

    public interface Factory {

        IntentAction<SingleSelectBuilder, Uri> createAction();
    }
}
