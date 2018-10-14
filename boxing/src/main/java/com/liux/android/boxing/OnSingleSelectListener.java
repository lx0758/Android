package com.liux.android.boxing;

import com.bilibili.boxing.model.entity.impl.ImageMedia;

/**
 * Created by Liux on 2017/11/13.
 */

public interface OnSingleSelectListener {

    /**
     * 图片单选回调
     * @param imageMedia
     */
    void onSingleSelect(ImageMedia imageMedia);
}
