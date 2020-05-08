package com.liux.android.mediaer.action;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.action.preview.PreviewDialog;
import com.liux.android.mediaer.builder.PreviewBuilder;

public class DefaultPreviewCallAction implements CallAction<PreviewBuilder> {
    @Override
    public void onStart(Context context, Fragment fragment, PreviewBuilder builder) throws MediaerException {
        new PreviewDialog(context, builder.medias, builder.position).show();
    }
}
