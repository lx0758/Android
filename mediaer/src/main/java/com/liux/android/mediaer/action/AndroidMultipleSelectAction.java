package com.liux.android.mediaer.action;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.fragment.app.Fragment;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.builder.MultipleSelectBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 由于 {@link Build.VERSION_CODES#JELLY_BEAN_MR2} 以下机器自带选择器没有多选能力，库类也没有去实现
 * 所以在某些机型多选功能用的是单选的结果
 * 并且目前 maxQuantity 也没有效果
 */
public class AndroidMultipleSelectAction implements IntentAction<MultipleSelectBuilder, List<Uri>> {
    @Override
    public void onStart(Context context, Fragment fragment, MultipleSelectBuilder builder, int requestCode) throws MediaerException {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
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
    public List<Uri> onFinish(Context context, MultipleSelectBuilder builder, int resultCode, Intent data) throws MediaerException {
        if (resultCode != Activity.RESULT_OK) throw new MediaerException(MediaerException.TYPE_CANCEL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && data.getClipData() != null) {
            List<Uri> uris = new ArrayList<>();
            ClipData clipdata = data.getClipData();
            for (int i = 0; i < clipdata.getItemCount(); i++) {
                uris.add(clipdata.getItemAt(i).getUri());
            }
            return uris;
        } else {
            return Collections.singletonList(data.getData());
        }
    }
}
