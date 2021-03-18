package com.liux.android.mediaer.action;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.builder.SingleSelectBuilder;

public class AndroidSingleSelectAction implements IntentAction<SingleSelectBuilder, Uri> {
    @Override
    public void onStart(Context context, Fragment fragment, SingleSelectBuilder builder, int requestCode) throws MediaerException {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType("image/*");
            if (intent.resolveActivity(context.getPackageManager()) == null) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            }
            fragment.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            throw new MediaerException(MediaerException.TYPE_INTENT, e);
        } catch (Exception e) {
            throw new MediaerException(MediaerException.TYPE_UNKNOWN, e);
        }
    }

    @Override
    public Uri onFinish(Context context, SingleSelectBuilder builder, int resultCode, Intent data) throws MediaerException {
        if (resultCode != Activity.RESULT_OK) throw new MediaerException(MediaerException.TYPE_CANCEL);
        return data.getData();
    }
}
