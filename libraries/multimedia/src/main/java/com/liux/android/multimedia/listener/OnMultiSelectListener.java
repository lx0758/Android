package com.liux.android.multimedia.listener;

import android.net.Uri;

import java.util.List;

/**
 * Created by Liux on 2017/11/13.
 */

public interface OnMultiSelectListener extends OnFailureListener {

    /**
     * 图片多选回调
     * @param uris
     */
    void onMultiSelect(List<Uri> uris);
}
