package com.liux.android.multimedia.listener;

import android.net.Uri;

/**
 * 拍照调用回调
 */
public interface OnRecordListener extends OnFailureListener {

    void onSucceed(Uri uri);
}
