package com.liux.android.multimedia;

import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.multimedia.builder.CropBuilder;
import com.liux.android.multimedia.builder.MultipleSelectBuilder;
import com.liux.android.multimedia.builder.PreviewBuilder;
import com.liux.android.multimedia.builder.RecordBuilder;
import com.liux.android.multimedia.builder.SingleSelectBuilder;
import com.liux.android.multimedia.builder.TakeBuilder;
import com.liux.android.multimedia.builder.VideoSelectBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Liux on 2017/11/13.
 */

public class Multimedia {
    public static final String TAG = "Multimedia";

    private FragmentActivity target;
    private static MultimediaConfig multimediaConfig = MultimediaConfig.create();

    public static MultimediaConfig config() {
        return multimediaConfig;
    }

    public static Multimedia with(FragmentActivity activity) {
        return new Multimedia(activity);
    }

    public static Multimedia with(Fragment fragment) {
        return new Multimedia(fragment.getActivity());
    }

    private Multimedia(FragmentActivity activity) {
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
     * @param maxQuantity
     * @return
     */
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

    /**
     * 预览图片
     * @param medias
     * @return
     */
    public PreviewBuilder previewForPath(String... medias) {
        return previewForPath(Arrays.asList(medias));
    }

    /**
     * 预览图片
     * @param medias
     * @return
     */
    public PreviewBuilder previewForPath(List<String> medias) {
        List<Uri> uris = new ArrayList<>();
        for (String media : medias) {
            if (media == null) continue;
            Uri uri;
            if (media.startsWith(File.separator)) {
                uri = Uri.fromFile(new File(media));
            } else {
                uri = Uri.parse(media);
            }
            uris.add(uri);
        }
        return preview(uris);
    }
}
