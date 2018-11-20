package com.liux.android.boxing;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;

import com.liux.android.boxing.request.CropRequest;
import com.liux.android.boxing.request.MultipleSelectRequest;
import com.liux.android.boxing.request.PreviewRequest;
import com.liux.android.boxing.request.RecordRequest;
import com.liux.android.boxing.request.SingleSelectRequest;
import com.liux.android.boxing.request.TakeRequest;
import com.liux.android.boxing.request.VideoSelectRequest;

public class Prepare {

    private Activity target;

    Prepare(Activity activity) {
        target = activity;
    }

    Prepare(Fragment fragment) {
        target = fragment.getActivity();
    }

    Prepare(android.support.v4.app.Fragment fragment) {
        target = fragment.getActivity();
    }

    /**
     * 图片单选
     * @return
     */
    public SingleSelectRequest singleSelect() {
        return new SingleSelectRequest(target);
    }

    /**
     * 图片多选
     * @param maxQuantity
     * @return
     */
    public MultipleSelectRequest multipleSelect(int maxQuantity) {
        return new MultipleSelectRequest(target, maxQuantity);
    }

    /**
     * 视频单选
     * @return
     */
    public VideoSelectRequest videoSelect() {
        return new VideoSelectRequest(target);
    }

    /**
     * .调用系统相机直接拍照
     * @param outUri 输出Uri
     * @return
     */
    public TakeRequest take(Uri outUri) {
        return new TakeRequest(target, outUri);
    }

    /**
     * .调用系统相机直接拍照
     * @param authority Uri权限
     * @return
     */
    public TakeRequest take(String authority) {
        return new TakeRequest(target, authority);
    }

    /**
     * .调用系统相机进行录像
     * @param outUri 输出Uri
     * @return
     */
    public RecordRequest record(Uri outUri) {
        return new RecordRequest(target, outUri);
    }

    /**
     * .调用系统相机进行录像
     * @param authority Uri权限
     * @return
     */
    public RecordRequest record(String authority) {
        return new RecordRequest(target, authority);
    }

    /**
     * 图片裁剪
     * @param inUri
     * @return
     */
    public CropRequest crop(Uri inUri) {
        return new CropRequest(target, inUri);
    }

    /**
     * 预览图片
     * @param medias
     * @return
     */
    public PreviewRequest preview(String... medias) {
        return new PreviewRequest(target, medias);
    }
}
