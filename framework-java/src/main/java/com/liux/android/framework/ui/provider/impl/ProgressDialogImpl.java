package com.liux.android.framework.ui.provider.impl;

import android.app.ProgressDialog;
import android.content.Context;

import com.liux.android.framework.ui.provider.IProgressDialog;

/**
 * 2018/3/8
 * By Liux
 * lx0758@qq.com
 */

public class ProgressDialogImpl implements IProgressDialog {

    private ProgressDialog mProgressDialog;

    public ProgressDialogImpl(Context context) {
        mProgressDialog = new ProgressDialog(context);
    }

    @Override
    public boolean isShowing() {
        return mProgressDialog.isShowing();
    }

    @Override
    public void show() {
        mProgressDialog.show();
    }

    @Override
    public void dismiss() {
        mProgressDialog.dismiss();
    }

    @Override
    public void cancel() {
        mProgressDialog.cancel();
    }

    @Override
    public void setCancelable(boolean cancelable) {
        mProgressDialog.setCancelable(cancelable);
    }

    @Override
    public void setOnCancelListener(OnCancelListener onCancelListener) {
        mProgressDialog.setOnCancelListener(dialog -> {
            if (onCancelListener != null) onCancelListener.onCancel(ProgressDialogImpl.this);
        });
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mProgressDialog.setOnDismissListener(dialog -> {
            if (onDismissListener != null) onDismissListener.onDismiss(ProgressDialogImpl.this);
        });
    }

    @Override
    public void setOnShowListener(OnShowListener onShowListener) {
        mProgressDialog.setOnShowListener(dialog -> {
            if (onShowListener != null) onShowListener.onShow(ProgressDialogImpl.this);
        });
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {

    }

    @Override
    public void setMax(int max) {
        mProgressDialog.setMax(max);
    }

    @Override
    public void setProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }
}
