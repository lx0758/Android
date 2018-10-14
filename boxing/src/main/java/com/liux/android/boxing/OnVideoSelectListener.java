package com.liux.android.boxing;

import com.bilibili.boxing.model.entity.impl.VideoMedia;

/**
 * Created by Liux on 2017/11/13.
 */

public interface OnVideoSelectListener {

    /**
     * 视频单选回调
     * @param videoMedia
     */
    void onVideoSelect(VideoMedia videoMedia);
}
