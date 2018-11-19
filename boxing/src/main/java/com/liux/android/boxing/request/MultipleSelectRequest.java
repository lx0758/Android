package com.liux.android.boxing.request;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.config.BoxingCropOption;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.liux.android.boxing.BoxingFragment;
import com.liux.android.boxing.BoxingUtil;
import com.liux.android.boxing.OnMultiSelectListener;
import com.liux.android.boxing.R;
import com.liux.android.boxing.Request;
import com.liux.android.boxing.Task;

import java.util.ArrayList;
import java.util.List;

public class MultipleSelectRequest extends Request {

    boolean useCamrea = true;
    int maxQuantity = 1;
    OnMultiSelectListener onMultiSelectListener;

    public MultipleSelectRequest(Activity target, int maxQuantity) {
        super(target);
        if (maxQuantity < 1) maxQuantity = 1;
        this.maxQuantity = maxQuantity;
    }

    public MultipleSelectRequest useCamera(boolean useCamera) {
        this.useCamrea = useCamera;
        return this;
    }

    public MultipleSelectRequest listener(OnMultiSelectListener onMultiSelectListener) {
        this.onMultiSelectListener = onMultiSelectListener;
        return this;
    }

    @Override
    public void start() {
        final BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG)
                .withMediaPlaceHolderRes(R.drawable.ic_boxing_default_image)
                .withMaxCount(maxQuantity)
                .needGif();

        if (useCamrea) {
            config.needCamera(R.drawable.ic_boxing_camera_white);
        }

        if (true) {
            config.withCropOption(new BoxingCropOption(new Uri.Builder()
                    .scheme("file")
                    .appendPath(BoxingUtil.getCacheDir(target).getAbsolutePath())
                    .appendPath(String.valueOf(System.currentTimeMillis()))
                    .build()));
        }

        BoxingFragment boxingFragment = BoxingUtil.getPermissionFragment(target);
        boxingFragment.executeTask(new Task() {
            @Override
            public void onMainThreadExecute(int requestCode, BoxingFragment boxingFragment) {
                Boxing.of(config).withIntent(boxingFragment.getActivity(), BoxingActivity.class).start(boxingFragment, requestCode);
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode != Activity.RESULT_OK) return;

                List<BaseMedia> medias = Boxing.getResult(data);
                if (medias == null) return;

                List<ImageMedia> imageMedias = new ArrayList<>();
                for (BaseMedia media : medias) {
                    imageMedias.add((ImageMedia) media);
                }
                onMultiSelectListener.onMultiSelect(imageMedias);
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            }
        });
    }
}
