package com.liux.android.multimedia.action;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.builder.Builder;

public interface IntentAction<B extends Builder, R> extends Action {

    void onStart(Context context, Fragment fragment, B builder, int requestCode) throws MultimediaException;

    R onFinish(Context context, B builder, int resultCode, Intent data) throws MultimediaException;
}
