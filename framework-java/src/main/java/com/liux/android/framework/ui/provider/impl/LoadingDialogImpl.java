package com.liux.android.framework.ui.provider.impl;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.liux.android.framework.ui.provider.ILoadingDialog;

/**
 * 2018/3/8
 * By Liux
 * lx0758@qq.com
 */

public class LoadingDialogImpl implements ILoadingDialog {

    private CharSequence mMessageContent;
    private ProgressDialog mProgressDialog;

    public LoadingDialogImpl(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
    }

    @Override
    public boolean isShowing() {
        return mProgressDialog.isShowing();
    }

    @Override
    public void show() {
        if (TextUtils.isEmpty(mMessageContent)) {
            mProgressDialog.setMessage("加载中，请稍等...");
        }
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
            if (onCancelListener != null) onCancelListener.onCancel(LoadingDialogImpl.this);
        });
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mProgressDialog.setOnDismissListener(dialog -> {
            if (onDismissListener != null) onDismissListener.onDismiss(LoadingDialogImpl.this);
        });
    }

    @Override
    public void setOnShowListener(OnShowListener onShowListener) {
        mProgressDialog.setOnShowListener(dialog -> {
            if (onShowListener != null) onShowListener.onShow(LoadingDialogImpl.this);
        });
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {

    }

    @Override
    public void setMessage(CharSequence message) {
        mMessageContent = message;
        mProgressDialog.setMessage(message);
    }

    @Override
    public void setMessage(int messageResId) {
        setMessage(mProgressDialog.getContext().getString(messageResId));
    }
}
