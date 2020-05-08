package com.liux.android.mediaer;

import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.mediaer.action.*;
import com.liux.android.mediaer.builder.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Liux on 2017/11/13.
 */

public class Mediaer {
    public static final String TAG = "Picturer";

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
        return new SingleSelectBuilder(config().getSingleSelectAction(), target);
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
        return new MultipleSelectBuilder(config().getMultipleSelectAction(), target, maxQuantity);
    }

    /**
     * 视频单选
     * @return
     */
    public VideoSelectBuilder videoSelect() {
        return new VideoSelectBuilder(config().getVideoSelectAction(), target);
    }

    /**
     * 拍照
     * @return
     */
    public TakeBuilder take() {
        return new TakeBuilder(config().getTakeAction(), target);
    }

    /**
     * .拍照
     * @param outUri 输出Uri
     * @return
     */
    public TakeBuilder take(Uri outUri) {
        return new TakeBuilder(config().getTakeAction(), target, outUri);
    }

    /**
     * 录像
     * @return
     */
    public RecordBuilder record() {
        return new RecordBuilder(config().getRecordAction(), target);
    }

    /**
     * 录像
     * @param outUri 输出Uri
     * @return
     */
    public RecordBuilder record(Uri outUri) {
        return new RecordBuilder(config().getRecordAction(), target, outUri);
    }

    /**
     * 图片裁剪
     * @param inUri
     * @return
     */
    public CropBuilder crop(Uri inUri) {
        return new CropBuilder(config().getCropAction(), target, inUri);
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
        return new PreviewBuilder(config().getPreviewCallAction(), target, medias);
    }

    public static class Config {

        public static Config create() {
            return new Config()
                    .setSingleSelectAction(new AndroidSingleSelectAction())
                    .setMultipleSelectAction(new AndroidMultipleSelectAction())
                    .setVideoSelectAction(new AndroidVideoSelectAction())
                    .setCropAction(new UCropCropAction())
                    .setTakeAction(new AndroidTakeAction())
                    .setRecordAction(new AndroidRecordAction())
                    .setPreviewCallAction(new DefaultPreviewCallAction());
        }

        private IntentAction<SingleSelectBuilder, Uri> singleSelectAction;
        private IntentAction<MultipleSelectBuilder, List<Uri>> multipleSelectAction;
        private IntentAction<VideoSelectBuilder, Uri> videoSelectAction;
        private IntentAction<CropBuilder, Uri> cropAction;
        private IntentAction<TakeBuilder, Uri> takeAction;
        private IntentAction<RecordBuilder, Uri> recordAction;
        private CallAction<PreviewBuilder> previewCallAction;

        public IntentAction<SingleSelectBuilder, Uri> getSingleSelectAction() {
            return singleSelectAction;
        }

        public Config setSingleSelectAction(IntentAction<SingleSelectBuilder, Uri> singleSelectAction) {
            this.singleSelectAction = singleSelectAction;
            return this;
        }

        public IntentAction<MultipleSelectBuilder, List<Uri>> getMultipleSelectAction() {
            return multipleSelectAction;
        }

        public Config setMultipleSelectAction(IntentAction<MultipleSelectBuilder, List<Uri>> multipleSelectAction) {
            this.multipleSelectAction = multipleSelectAction;
            return this;
        }

        public IntentAction<VideoSelectBuilder, Uri> getVideoSelectAction() {
            return videoSelectAction;
        }

        public Config setVideoSelectAction(IntentAction<VideoSelectBuilder, Uri> videoSelectAction) {
            this.videoSelectAction = videoSelectAction;
            return this;
        }

        public IntentAction<CropBuilder, Uri> getCropAction() {
            return cropAction;
        }

        public Config setCropAction(IntentAction<CropBuilder, Uri> cropAction) {
            this.cropAction = cropAction;
            return this;
        }

        public IntentAction<TakeBuilder, Uri> getTakeAction() {
            return takeAction;
        }

        public Config setTakeAction(IntentAction<TakeBuilder, Uri> takeAction) {
            this.takeAction = takeAction;
            return this;
        }

        public IntentAction<RecordBuilder, Uri> getRecordAction() {
            return recordAction;
        }

        public Config setRecordAction(IntentAction<RecordBuilder, Uri> recordAction) {
            this.recordAction = recordAction;
            return this;
        }

        public CallAction<PreviewBuilder> getPreviewCallAction() {
            return previewCallAction;
        }

        public Config setPreviewCallAction(CallAction<PreviewBuilder> previewCallAction) {
            this.previewCallAction = previewCallAction;
            return this;
        }
    }
}
