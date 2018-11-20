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
import com.liux.android.boxing.OnSingleSelectListener;
import com.liux.android.boxing.R;
import com.liux.android.boxing.Request;
import com.liux.android.boxing.Task;

import java.util.List;

public class SingleSelectRequest extends Request {

    boolean useCamrea = true, useCrop = true;
    OnSingleSelectListener onSingleSelectListener;

    public SingleSelectRequest(Activity target) {
        super(target);
    }

    public SingleSelectRequest useCamera(boolean useCamera) {
        this.useCamrea = useCamera;
        return this;
    }

    public SingleSelectRequest useCrop(boolean useCrop) {
        this.useCrop = useCrop;
        return this;
    }

    public SingleSelectRequest listener(OnSingleSelectListener onSingleSelectListener) {
        this.onSingleSelectListener = onSingleSelectListener;
        return this;
    }

    @Override
    public void start() {
        final BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
                .withMediaPlaceHolderRes(R.drawable.ic_boxing_default_image)
                .needGif();

        if (useCamrea) {
            config.needCamera(R.drawable.ic_boxing_camera_white);
        }

        if (useCrop) {
            config.withCropOption(new BoxingCropOption(new Uri.Builder()
                    .scheme("file")
                    .appendPath(BoxingUtil.getCacheTempFile(target).getAbsolutePath())
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

                if (onSingleSelectListener != null) onSingleSelectListener.onSingleSelect((ImageMedia) medias.get(0));
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            }
        });
    }
}
