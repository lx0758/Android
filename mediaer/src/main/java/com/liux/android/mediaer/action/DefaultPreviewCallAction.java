package com.liux.android.mediaer.action;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;

import androidx.fragment.app.Fragment;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.R;
import com.liux.android.mediaer.builder.PreviewBuilder;

import java.util.List;

public class DefaultPreviewCallAction implements CallAction<PreviewBuilder> {
    @Override
    public void onStart(Context context, Fragment fragment, PreviewBuilder builder) throws MediaerException {
        new PreviewDialog(context, builder.medias, builder.position).show();
    }

    private static class PreviewDialog extends Dialog {

        private List<Uri> medias;
        private int position;

        public PreviewDialog(Context context, List<Uri> medias, int position) {
            super(context);
            this.medias = medias;
            this.position = position;
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            getWindow().getDecorView().setPadding(0, 0, 0, 0);
            getWindow().setGravity(Gravity.BOTTOM);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_preview);
        }
    }
}
