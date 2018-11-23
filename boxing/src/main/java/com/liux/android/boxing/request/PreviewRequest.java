package com.liux.android.boxing.request;

import android.app.Activity;
import android.content.Intent;

import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing_impl.ui.BoxingPreviewActivity;
import com.liux.android.boxing.OnCancelListener;
import com.liux.android.boxing.Request;

import java.util.ArrayList;

public class PreviewRequest extends Request<PreviewRequest> {

    String[] medias;
    int position;

    public PreviewRequest(Activity target, String[] medias) {
        super(target);
        this.medias = medias;
    }

    public PreviewRequest position(int position) {
        if (position < 0) position = 0;
        if (position >= medias.length) position = medias.length - 1;
        this.position = position;
        return this;
    }

    @Override
    public PreviewRequest listener(OnCancelListener onCancelListener) {
        return super.listener(onCancelListener);
    }

    @Override
    public void start() {
        ArrayList<BaseMedia> medias = new ArrayList<>();
        for (String media : this.medias) {
            medias.add(new ImageMedia("", media));
        }

        Intent intent = new Intent(target, BoxingPreviewActivity.class);
        intent.putParcelableArrayListExtra(BoxingPreviewActivity.PARAM_IMAGES, medias);
        intent.putExtra(BoxingPreviewActivity.PARAM_POSITION, position);
        target.startActivity(intent);
    }
}
