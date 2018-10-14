package com.liux.android.boxing;

import com.bilibili.boxing.model.entity.impl.ImageMedia;

import java.util.List;

/**
 * Created by Liux on 2017/11/13.
 */

public interface OnMultiSelectListener {

    /**
     * 图片多选回调
     * @param imageMedias
     */
    void onMultiSelect(List<ImageMedia> imageMedias);
}
