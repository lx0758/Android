package com.liux.android.mediaer.builder;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.action.IntentAction;
import com.liux.android.mediaer.inject.IntentTask;
import com.liux.android.mediaer.listener.OnCancelListener;
import com.liux.android.mediaer.listener.OnFailureListener;
import com.liux.android.mediaer.listener.OnMultiSelectListener;

import java.util.List;

public class MultipleSelectBuilder extends Builder<MultipleSelectBuilder, IntentAction<MultipleSelectBuilder, List<Uri>>> {

    public int maxQuantity = 1;

    public OnMultiSelectListener onMultiSelectListener;

    public MultipleSelectBuilder(IntentAction<MultipleSelectBuilder, List<Uri>> action, FragmentActivity target, int maxQuantity) {
        super(action, target);
        if (maxQuantity < 1) maxQuantity = 1;
        this.maxQuantity = maxQuantity;
    }

    public MultipleSelectBuilder listener(OnMultiSelectListener onMultiSelectListener) {
        this.onMultiSelectListener = onMultiSelectListener;
        return this;
    }

    @Override
    public MultipleSelectBuilder listener(OnCancelListener onCancelListener) {
        return super.listener(onCancelListener);
    }

    @Override
    String[] onDeclarePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        return null;
    }

    @Override
    OnFailureListener onDeclareFailureListener() {
        return onMultiSelectListener;
    }

    @Override
    void onStart() {
        executeTask(new IntentTask() {
            @Override
            public void onExecute(Fragment fragment, int requestCode) {
                try {
                    action.onStart(target, fragment, MultipleSelectBuilder.this, requestCode);
                } catch (MediaerException e) {
                    onFailure(e);
                }
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {
                try {
                    List<Uri> result = action.onFinish(target, MultipleSelectBuilder.this, resultCode, data);
                    if (onMultiSelectListener != null) onMultiSelectListener.onMultiSelect(result);
                } catch (MediaerException e) {
                    onFailure(e);
                }
            }
        });
    }

    public interface Factory {

        IntentAction<MultipleSelectBuilder, List<Uri>> createAction();
    }
}
