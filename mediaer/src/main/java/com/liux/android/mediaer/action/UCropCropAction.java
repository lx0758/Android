package com.liux.android.mediaer.action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.MediaerUtil;
import com.liux.android.mediaer.builder.CropBuilder;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.BitmapLoadUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UCropCropAction implements IntentAction<CropBuilder, Uri> {

    @Override
    public void onStart(Context context, Fragment fragment, CropBuilder builder, int requestCode) throws MediaerException {
        UCrop uCrop = UCrop.of(builder.inUri, Uri.fromFile(MediaerUtil.getCacheTempFile(context)));
        if (builder.maxWidth > 0 && builder.maxHeight > 0) {
            uCrop.withMaxResultSize(builder.maxWidth, builder.maxHeight);
        }
        if (builder.aspectRatioX > 0 && builder.aspectRatioY > 0) {
            uCrop.withAspectRatio(builder.aspectRatioX, builder.aspectRatioY);
        }
        uCrop.start(context, fragment, requestCode);
    }

    @Override
    public Uri onFinish(Context context, CropBuilder builder, int resultCode, Intent data) throws MediaerException {
        switch (resultCode) {
            case Activity.RESULT_OK:
                try {
                    copyFileToUri(context, new File(UCrop.getOutput(data).getPath()), builder.outUri);
                } catch (IOException e) {
                    throw new MediaerException(MediaerException.TYPE_UNKNOWN, e);
                }
                return builder.outUri;
            case Activity.RESULT_CANCELED:
                throw new MediaerException(MediaerException.TYPE_CANCEL, null);
            case UCrop.RESULT_ERROR:
                throw new MediaerException(MediaerException.TYPE_UNKNOWN, UCrop.getError(data));
        }
        throw new MediaerException(MediaerException.TYPE_UNKNOWN, null);
    }

    private void copyFileToUri(Context context, File source, Uri target) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            switch (target.getScheme()) {
                case "file":
                    outputStream = new FileOutputStream(target.getPath());
                    break;
                case "content":
                    outputStream = context.getContentResolver().openOutputStream(target);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Uri scheme:" + target.getScheme());
            }

            byte buffer[] = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            BitmapLoadUtils.close(outputStream);
            BitmapLoadUtils.close(inputStream);
        }
    }
}
