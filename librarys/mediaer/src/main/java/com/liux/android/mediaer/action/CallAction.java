package com.liux.android.mediaer.action;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.builder.Builder;

public interface CallAction<B extends Builder> extends Action {

    void onStart(Context context, Fragment fragment, B builder) throws MediaerException;
}
