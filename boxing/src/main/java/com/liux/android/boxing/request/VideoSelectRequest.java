package com.liux.android.boxing.request;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.VideoMedia;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.liux.android.boxing.BoxingFragment;
import com.liux.android.boxing.BoxingUtil;
import com.liux.android.boxing.OnVideoSelectListener;
import com.liux.android.boxing.R;
import com.liux.android.boxing.Request;
import com.liux.android.boxing.Task;

import java.util.List;

public class VideoSelectRequest extends Request {

    OnVideoSelectListener onVideoSelectListener;

    public VideoSelectRequest(Activity target) {
        super(target);
    }

    public VideoSelectRequest listener(OnVideoSelectListener onVideoSelectListener) {
        this.onVideoSelectListener = onVideoSelectListener;
        return this;
    }

    @Override
    public void start() {
        final BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.VIDEO)
                .withVideoDurationRes(R.drawable.ic_boxing_play);

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

                onVideoSelectListener.onVideoSelect((VideoMedia) medias.get(0));
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            }
        });
    }
}
