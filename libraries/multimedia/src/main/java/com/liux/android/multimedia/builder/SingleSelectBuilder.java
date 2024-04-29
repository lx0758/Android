package com.liux.android.multimedia.builder;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.multimedia.Multimedia;
import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.action.IntentAction;
import com.liux.android.multimedia.inject.IntentTask;
import com.liux.android.multimedia.listener.OnCancelListener;
import com.liux.android.multimedia.listener.OnCropListener;
import com.liux.android.multimedia.listener.OnFailureListener;
import com.liux.android.multimedia.listener.OnSingleSelectListener;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        return null;
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
                } catch (MultimediaException e) {
                    onFailure(e);
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                try {
                    Uri result = action.onFinish(target, SingleSelectBuilder.this, resultCode, data);
                    if (useCrop) {
                        Multimedia.with(target)
                                .crop(result)
                                .listener(onCancelListener)
                                .listener(new OnCropListener() {
                                    @Override
                                    public void onSucceed(Uri output) {
                                        if (onSingleSelectListener != null) onSingleSelectListener.onSingleSelect(output);
                                    }

                                    @Override
                                    public void onFailure(MultimediaException e) {
                                        SingleSelectBuilder.this.onFailure(e);
                                    }
                                })
                                .start();
                        return;
                    }
                    if (onSingleSelectListener != null) onSingleSelectListener.onSingleSelect(result);
                } catch (MultimediaException e) {
                    onFailure(e);
                }
            }
        });
    }

    public interface Factory {

        IntentAction<SingleSelectBuilder, Uri> createAction();
    }
}
