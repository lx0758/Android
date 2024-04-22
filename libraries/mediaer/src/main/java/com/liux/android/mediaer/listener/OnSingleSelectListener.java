package com.liux.android.mediaer.listener;

import android.net.Uri;

/**
 * Created by Liux on 2017/11/13.
 */

public interface OnSingleSelectListener extends OnFailureListener {

    /**
     * 图片单选回调
     * @param uri
     */
    void onSingleSelect(Uri uri);
}
