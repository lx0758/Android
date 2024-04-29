package com.liux.android.multimedia.action;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.builder.Builder;

public interface CallAction<B extends Builder> extends Action {

    void onStart(Context context, Fragment fragment, B builder) throws MultimediaException;
}
