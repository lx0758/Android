package com.liux.android.multimedia.builder;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.action.IntentAction;
import com.liux.android.multimedia.inject.IntentTask;
import com.liux.android.multimedia.listener.OnCancelListener;
import com.liux.android.multimedia.listener.OnFailureListener;
import com.liux.android.multimedia.listener.OnVideoSelectListener;

public class VideoSelectBuilder extends Builder<VideoSelectBuilder, IntentAction<VideoSelectBuilder, Uri>> {

    public OnVideoSelectListener onVideoSelectListener;

    public VideoSelectBuilder(IntentAction<VideoSelectBuilder, Uri> action, FragmentActivity target) {
        super(action, target);
    }

    public VideoSelectBuilder listener(OnVideoSelectListener onVideoSelectListener) {
        this.onVideoSelectListener = onVideoSelectListener;
        return this;
    }

    @Override
    public VideoSelectBuilder listener(OnCancelListener onCancelListener) {
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
        return onVideoSelectListener;
    }

    @Override
    void onStart() {
        executeTask(new IntentTask() {
            @Override
            public void onExecute(Fragment fragment, int requestCode) {
                try {
                    action.onStart(target, fragment, VideoSelectBuilder.this, requestCode);
                } catch (MultimediaException e) {
                    onFailure(e);
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                try {
                    Uri result = action.onFinish(target, VideoSelectBuilder.this, resultCode, data);
                    if (onVideoSelectListener != null) onVideoSelectListener.onVideoSelect(result);
                } catch (MultimediaException e) {
                    onFailure(e);
                }
            }
        });
    }

    public interface Factory {

        IntentAction<VideoSelectBuilder, Uri> createAction();
    }
}
