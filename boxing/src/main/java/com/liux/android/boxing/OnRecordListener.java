package com.liux.android.boxing;

import android.net.Uri;

/**
 * 拍照调用回调
 */
public interface OnRecordListener {

    /* 没有找到应用程序 */
    int ERROR_INTENT = 1;
    /* 没有拍照权限 */
    int ERROR_PERMISSION = 2;

    void onSucceed(Uri uri);

    void onFailure(int type);
}
