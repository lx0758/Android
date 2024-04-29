package com.liux.android.multimedia.builder;

import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.action.CallAction;
import com.liux.android.multimedia.inject.CallTask;
import com.liux.android.multimedia.listener.OnCancelListener;
import com.liux.android.multimedia.listener.OnFailureListener;

import java.util.List;

public class PreviewBuilder extends Builder<PreviewBuilder, CallAction<PreviewBuilder>> {

    public List<Uri> medias;
    public int position;

    public PreviewBuilder(CallAction<PreviewBuilder> action, FragmentActivity target, List<Uri> medias) {
        super(action, target);
        this.medias = medias;
    }

    public PreviewBuilder position(int position) {
        if (position < 0) position = 0;
        if (position >= medias.size()) position = medias.size() - 1;
        this.position = position;
        return this;
    }

    @Override
    public PreviewBuilder listener(OnCancelListener onCancelListener) {
        return super.listener(onCancelListener);
    }

    @Override
    String[] onDeclarePermissions() {
        return null;
    }

    @Override
    OnFailureListener onDeclareFailureListener() {
        return null;
    }

    @Override
    void onStart() {
        if (medias == null || medias.isEmpty()) {
            onFailure(new MultimediaException(MultimediaException.TYPE_UNKNOWN));
            return;
        }
        if (position < 0) position = 0;
        if (position >= medias.size()) position = medias.size() - 1;
        executeTask(new CallTask() {
            @Override
            public void onExecute(Fragment fragment) {
                try {
                    action.onStart(target, fragment, PreviewBuilder.this);
                } catch (MultimediaException e) {
                    onFailure(e);
                }
            }
        });
    }

    public interface Factory {

        CallAction<PreviewBuilder> createAction();
    }
}
