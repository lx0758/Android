package com.liux.android.mediaer.builder;

import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.action.CallAction;
import com.liux.android.mediaer.inject.CallTask;
import com.liux.android.mediaer.listener.OnCancelListener;
import com.liux.android.mediaer.listener.OnFailureListener;

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
        executeTask(new CallTask() {
            @Override
            public void onExecute(Fragment fragment) {
                try {
                    action.onStart(target, fragment, PreviewBuilder.this);
                } catch (MediaerException e) {
                    onFailure(e);
                }
            }
        });
    }
}
