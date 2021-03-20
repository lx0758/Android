package org.android.framework.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatDialog;

import org.android.framework.R;

public class PhotoSelectDialog extends AppCompatDialog {

    public static Builder with(Context context) {
        return new Builder(context);
    }

    private Builder builder;

    private PhotoSelectDialog(Builder builder) {
        super(builder.context);
        this.builder = builder;
        getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(R.layout.dialog_photo_select);

        findViewById(R.id.ll_preview).setVisibility(builder.preview ? View.VISIBLE : View.GONE);
        findViewById(R.id.tv_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder.onPreviewListener != null) builder.onPreviewListener.onPreview();
                dismiss();
            }
        });
        findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder.onSelectListener != null) builder.onSelectListener.onCamera();
                dismiss();
            }
        });
        findViewById(R.id.tv_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder.onSelectListener != null) builder.onSelectListener.onAlbum();
                dismiss();
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public static class Builder {

        Context context;
        boolean preview = false;
        OnSelectListener onSelectListener;
        OnPreviewListener onPreviewListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder preview(boolean preview) {
            this.preview = preview;
            return this;
        }

        public Builder listener(OnSelectListener onSelectListener) {
            this.onSelectListener = onSelectListener;
            return this;
        }

        public Builder listener(OnSelectListener onSelectListener, OnPreviewListener onPreviewListener) {
            listener(onSelectListener);
            this.onPreviewListener = onPreviewListener;
            return this;
        }

        public void show() {
            new PhotoSelectDialog(this).show();
        }
    }

    public interface OnSelectListener {

        void onCamera();

        void onAlbum();
    }

    public interface OnPreviewListener {

        void onPreview();
    }
}