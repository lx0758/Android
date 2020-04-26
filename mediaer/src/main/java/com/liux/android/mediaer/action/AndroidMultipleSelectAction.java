package com.liux.android.mediaer.action;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.builder.MultipleSelectBuilder;

import java.util.Collections;
import java.util.List;

public class AndroidMultipleSelectAction implements IntentAction<MultipleSelectBuilder, List<Uri>> {
    @Override
    public void onStart(Context context, Fragment fragment, MultipleSelectBuilder builder, int requestCode) throws MediaerException {
        // FIXME: 2020/4/26 系统不支持多选, 暂时用单选代替
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            fragment.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            throw new MediaerException(MediaerException.TYPE_INTENT, e);
        } catch (Exception e) {
            throw new MediaerException(MediaerException.TYPE_UNKNOWN, e);
        }
    }

    @Override
    public List<Uri> onFinish(Context context, MultipleSelectBuilder builder, int resultCode, Intent data) throws MediaerException {
        if (resultCode != Activity.RESULT_OK) throw new MediaerException(MediaerException.TYPE_CANCEL, null);
        return Collections.singletonList(data.getData());
    }
}
