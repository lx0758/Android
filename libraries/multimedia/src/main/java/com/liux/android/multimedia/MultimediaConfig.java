package com.liux.android.multimedia;

import android.net.Uri;

import com.liux.android.multimedia.action.AndroidMultipleSelectAction;
import com.liux.android.multimedia.action.AndroidRecordAction;
import com.liux.android.multimedia.action.AndroidSingleSelectAction;
import com.liux.android.multimedia.action.AndroidTakeAction;
import com.liux.android.multimedia.action.AndroidVideoSelectAction;
import com.liux.android.multimedia.action.CallAction;
import com.liux.android.multimedia.action.DefaultPreviewAction;
import com.liux.android.multimedia.action.IntentAction;
import com.liux.android.multimedia.action.UCropCropAction;
import com.liux.android.multimedia.builder.CropBuilder;
import com.liux.android.multimedia.builder.MultipleSelectBuilder;
import com.liux.android.multimedia.builder.PreviewBuilder;
import com.liux.android.multimedia.builder.RecordBuilder;
import com.liux.android.multimedia.builder.SingleSelectBuilder;
import com.liux.android.multimedia.builder.TakeBuilder;
import com.liux.android.multimedia.builder.VideoSelectBuilder;

import java.util.List;

public class MultimediaConfig {

    public static MultimediaConfig create() {
        return new MultimediaConfig()
                .setSingleSelectFactory(new SingleSelectBuilder.Factory() {
                    @Override
                    public IntentAction<SingleSelectBuilder, Uri> createAction() {
                        return new AndroidSingleSelectAction();
                    }
                })
                .setMultipleSelectFactory(new MultipleSelectBuilder.Factory() {
                    @Override
                    public IntentAction<MultipleSelectBuilder, List<Uri>> createAction() {
                        return new AndroidMultipleSelectAction();
                    }
                })
                .setVideoSelectFactory(new VideoSelectBuilder.Factory() {
                    @Override
                    public IntentAction<VideoSelectBuilder, Uri> createAction() {
                        return new AndroidVideoSelectAction();
                    }
                })
                .setCropFactory(new CropBuilder.Factory() {
                    @Override
                    public IntentAction<CropBuilder, Uri> createAction() {
                        return new UCropCropAction();
                    }
                })
                .setTakeFactory(new TakeBuilder.Factory() {
                    @Override
                    public IntentAction<TakeBuilder, Uri> createAction() {
                        return new AndroidTakeAction();
                    }
                })
                .setRecordFactory(new RecordBuilder.Factory() {
                    @Override
                    public IntentAction<RecordBuilder, Uri> createAction() {
                        return new AndroidRecordAction();
                    }
                })
                .setPreviewFactory(new PreviewBuilder.Factory() {
                    @Override
                    public CallAction<PreviewBuilder> createAction() {
                        return new DefaultPreviewAction();
                    }
                });
    }

    private SingleSelectBuilder.Factory singleSelectFactory;
    private MultipleSelectBuilder.Factory multipleSelectFactory;
    private VideoSelectBuilder.Factory videoSelectFactory;
    private CropBuilder.Factory cropFactory;
    private TakeBuilder.Factory takeFactory;
    private RecordBuilder.Factory recordFactory;
    private PreviewBuilder.Factory previewFactory;

    public SingleSelectBuilder.Factory getSingleSelectFactory() {
        return singleSelectFactory;
    }

    public MultimediaConfig setSingleSelectFactory(SingleSelectBuilder.Factory singleSelectFactory) {
        this.singleSelectFactory = singleSelectFactory;
        return this;
    }

    public MultipleSelectBuilder.Factory getMultipleSelectFactory() {
        return multipleSelectFactory;
    }

    public MultimediaConfig setMultipleSelectFactory(MultipleSelectBuilder.Factory multipleSelectFactory) {
        this.multipleSelectFactory = multipleSelectFactory;
        return this;
    }

    public VideoSelectBuilder.Factory getVideoSelectFactory() {
        return videoSelectFactory;
    }

    public MultimediaConfig setVideoSelectFactory(VideoSelectBuilder.Factory videoSelectFactory) {
        this.videoSelectFactory = videoSelectFactory;
        return this;
    }

    public CropBuilder.Factory getCropFactory() {
        return cropFactory;
    }

    public MultimediaConfig setCropFactory(CropBuilder.Factory cropFactory) {
        this.cropFactory = cropFactory;
        return this;
    }

    public TakeBuilder.Factory getTakeFactory() {
        return takeFactory;
    }

    public MultimediaConfig setTakeFactory(TakeBuilder.Factory takeFactory) {
        this.takeFactory = takeFactory;
        return this;
    }

    public RecordBuilder.Factory getRecordFactory() {
        return recordFactory;
    }

    public MultimediaConfig setRecordFactory(RecordBuilder.Factory recordFactory) {
        this.recordFactory = recordFactory;
        return this;
    }

    public PreviewBuilder.Factory getPreviewFactory() {
        return previewFactory;
    }

    public MultimediaConfig setPreviewFactory(PreviewBuilder.Factory previewFactory) {
        this.previewFactory = previewFactory;
        return this;
    }
}
