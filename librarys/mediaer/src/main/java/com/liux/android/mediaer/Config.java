package com.liux.android.mediaer;

import android.net.Uri;

import com.liux.android.mediaer.action.AndroidMultipleSelectAction;
import com.liux.android.mediaer.action.AndroidRecordAction;
import com.liux.android.mediaer.action.AndroidSingleSelectAction;
import com.liux.android.mediaer.action.AndroidTakeAction;
import com.liux.android.mediaer.action.AndroidVideoSelectAction;
import com.liux.android.mediaer.action.CallAction;
import com.liux.android.mediaer.action.DefaultPreviewAction;
import com.liux.android.mediaer.action.IntentAction;
import com.liux.android.mediaer.action.UCropCropAction;
import com.liux.android.mediaer.builder.CropBuilder;
import com.liux.android.mediaer.builder.MultipleSelectBuilder;
import com.liux.android.mediaer.builder.PreviewBuilder;
import com.liux.android.mediaer.builder.RecordBuilder;
import com.liux.android.mediaer.builder.SingleSelectBuilder;
import com.liux.android.mediaer.builder.TakeBuilder;
import com.liux.android.mediaer.builder.VideoSelectBuilder;

import java.util.List;

public class Config {

    public static Config create() {
        return new Config()
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

    public Config setSingleSelectFactory(SingleSelectBuilder.Factory singleSelectFactory) {
        this.singleSelectFactory = singleSelectFactory;
        return this;
    }

    public MultipleSelectBuilder.Factory getMultipleSelectFactory() {
        return multipleSelectFactory;
    }

    public Config setMultipleSelectFactory(MultipleSelectBuilder.Factory multipleSelectFactory) {
        this.multipleSelectFactory = multipleSelectFactory;
        return this;
    }

    public VideoSelectBuilder.Factory getVideoSelectFactory() {
        return videoSelectFactory;
    }

    public Config setVideoSelectFactory(VideoSelectBuilder.Factory videoSelectFactory) {
        this.videoSelectFactory = videoSelectFactory;
        return this;
    }

    public CropBuilder.Factory getCropFactory() {
        return cropFactory;
    }

    public Config setCropFactory(CropBuilder.Factory cropFactory) {
        this.cropFactory = cropFactory;
        return this;
    }

    public TakeBuilder.Factory getTakeFactory() {
        return takeFactory;
    }

    public Config setTakeFactory(TakeBuilder.Factory takeFactory) {
        this.takeFactory = takeFactory;
        return this;
    }

    public RecordBuilder.Factory getRecordFactory() {
        return recordFactory;
    }

    public Config setRecordFactory(RecordBuilder.Factory recordFactory) {
        this.recordFactory = recordFactory;
        return this;
    }

    public PreviewBuilder.Factory getPreviewFactory() {
        return previewFactory;
    }

    public Config setPreviewFactory(PreviewBuilder.Factory previewFactory) {
        this.previewFactory = previewFactory;
        return this;
    }
}
