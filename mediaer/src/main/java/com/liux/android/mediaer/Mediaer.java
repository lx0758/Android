package com.liux.android.mediaer;

import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.mediaer.builder.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Liux on 2017/11/13.
 */

public class Mediaer {
    public static final String TAG = "Mediaer";

    private FragmentActivity target;
    private static Config config = Config.create();

    public static Config config() {
        return config;
    }

    public static Mediaer with(FragmentActivity activity) {
        return new Mediaer(activity);
    }

    public static Mediaer with(Fragment fragment) {
        return new Mediaer(fragment.getActivity());
    }

    private Mediaer(FragmentActivity activity) {
        target = activity;
    }

    /**
     * 图片单选
     * @return
     */
    public SingleSelectBuilder singleSelect() {
        return new SingleSelectBuilder(config().getSingleSelectFactory().createAction(), target);
    }

    /**
     * 图片多选
     * 由于系统自带选择器没有多选能力, 并且本库类也没有去实现
     * 多选功能用的是单选的结果
     * @param maxQuantity
     * @return
     */
    @Deprecated
    public MultipleSelectBuilder multipleSelect(int maxQuantity) {
        return new MultipleSelectBuilder(config().getMultipleSelectFactory().createAction(), target, maxQuantity);
    }

    /**
     * 视频单选
     * @return
     */
    public VideoSelectBuilder videoSelect() {
        return new VideoSelectBuilder(config().getVideoSelectFactory().createAction(), target);
    }

    /**
     * 拍照
     * @return
     */
    public TakeBuilder take() {
        return new TakeBuilder(config().getTakeFactory().createAction(), target);
    }

    /**
     * .拍照
     * @param outUri 输出Uri
     * @return
     */
    public TakeBuilder take(Uri outUri) {
        return new TakeBuilder(config().getTakeFactory().createAction(), target, outUri);
    }

    /**
     * 录像
     * @return
     */
    public RecordBuilder record() {
        return new RecordBuilder(config().getRecordFactory().createAction(), target);
    }

    /**
     * 录像
     * @param outUri 输出Uri
     * @return
     */
    public RecordBuilder record(Uri outUri) {
        return new RecordBuilder(config().getRecordFactory().createAction(), target, outUri);
    }

    /**
     * 图片裁剪
     * @param inUri
     * @return
     */
    public CropBuilder crop(Uri inUri) {
        return new CropBuilder(config().getCropFactory().createAction(), target, inUri);
    }

    /**
     * 预览图片
     * @param medias
     * @return
     */
    public PreviewBuilder preview(Uri... medias) {
        return preview(Arrays.asList(medias));
    }

    /**
     * 预览图片
     * @param medias
     * @return
     */
    public PreviewBuilder preview(List<Uri> medias) {
        return new PreviewBuilder(config().getPreviewFactory().createAction(), target, medias);
    }
}
