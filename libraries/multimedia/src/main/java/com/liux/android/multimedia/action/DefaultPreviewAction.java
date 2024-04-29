package com.liux.android.multimedia.action;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.action.preview.PreviewDialog;
import com.liux.android.multimedia.builder.PreviewBuilder;

public class DefaultPreviewAction implements CallAction<PreviewBuilder> {
    @Override
    public void onStart(Context context, Fragment fragment, PreviewBuilder builder) throws MultimediaException {
        new PreviewDialog(context, builder.medias, builder.position).show();
    }
}
