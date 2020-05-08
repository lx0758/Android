package com.liux.android.mediaer.action;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.builder.VideoSelectBuilder;

public class AndroidVideoSelectAction implements IntentAction<VideoSelectBuilder, Uri> {
    @Override
    public void onStart(Context context, Fragment fragment, VideoSelectBuilder builder, int requestCode) throws MediaerException {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            fragment.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            throw new MediaerException(MediaerException.TYPE_INTENT, e);
        } catch (Exception e) {
            throw new MediaerException(MediaerException.TYPE_UNKNOWN, e);
        }
    }

    @Override
    public Uri onFinish(Context context, VideoSelectBuilder builder, int resultCode, Intent data) throws MediaerException {
        if (resultCode != Activity.RESULT_OK) throw new MediaerException(MediaerException.TYPE_CANCEL, null);
        return data.getData();
    }
}
