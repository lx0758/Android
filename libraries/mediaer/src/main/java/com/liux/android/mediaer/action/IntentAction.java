package com.liux.android.mediaer.action;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.builder.Builder;

public interface IntentAction<B extends Builder, R> extends Action {

    void onStart(Context context, Fragment fragment, B builder, int requestCode) throws MediaerException;

    R onFinish(Context context, B builder, int resultCode, Intent data) throws MediaerException;
}
