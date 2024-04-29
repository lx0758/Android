package com.liux.android.multimedia.action;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;

import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.builder.RecordBuilder;

public class AndroidRecordAction implements IntentAction<RecordBuilder, Uri> {
    @Override
    public void onStart(Context context, Fragment fragment, RecordBuilder builder, int requestCode) throws MultimediaException {
        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            if (builder.size > 0) intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, builder.size);
            if (builder.quality > 0) intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, builder.quality);
            if (builder.duration > 0) intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, builder.duration);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, builder.outUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, builder.outUri));
            }
            fragment.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            throw new MultimediaException(MultimediaException.TYPE_INTENT, e);
        } catch (Exception e) {
            throw new MultimediaException(MultimediaException.TYPE_UNKNOWN, e);
        }
    }

    @Override
    public Uri onFinish(Context context, RecordBuilder builder, int resultCode, Intent data) throws MultimediaException {
        if (resultCode != Activity.RESULT_OK) throw new MultimediaException(MultimediaException.TYPE_CANCEL);
        return builder.outUri;
    }
}
