package com.liux.android.multimedia.listener;

import android.net.Uri;

/**
 * Created by Liux on 2017/11/13.
 */

public interface OnVideoSelectListener extends OnFailureListener {

    /**
     * 视频单选回调
     * @param uri
     */
    void onVideoSelect(Uri uri);
}
