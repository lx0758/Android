package com.liux.android.boxing;

import android.net.Uri;

/**
 * 裁剪结果回调
 */
public interface OnCropListener {

    void onSucceed(Uri output);

    void onFailure();
}
