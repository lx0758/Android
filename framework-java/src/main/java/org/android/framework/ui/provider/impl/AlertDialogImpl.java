package org.android.framework.ui.provider.impl;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import org.android.framework.ui.provider.IAlertDialog;

/**
 * 2018/3/8
 * By Liux
 * lx0758@qq.com
 */

public class AlertDialogImpl implements IAlertDialog {
    
    private AlertDialog mAlertDialog;

    public AlertDialogImpl(Context context) {
        mAlertDialog = new AlertDialog.Builder(context).create();
    }

    @Override
    public boolean isShowing() {
        return mAlertDialog.isShowing();
    }

    @Override
    public void show() {
        mAlertDialog.show();
    }

    @Override
    public void dismiss() {
        mAlertDialog.dismiss();
    }

    @Override
    public void cancel() {
        mAlertDialog.cancel();
    }

    @Override
    public void setCancelable(boolean cancelable) {
        mAlertDialog.setCancelable(cancelable);
    }

    @Override
    public void setOnCancelListener(OnCancelListener onCancelListener) {
        mAlertDialog.setOnCancelListener(dialog -> {
            if (onCancelListener != null) onCancelListener.onCancel(AlertDialogImpl.this);
        });
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mAlertDialog.setOnDismissListener(dialog -> {
            if (onDismissListener != null) onDismissListener.onDismiss(AlertDialogImpl.this);
        });
    }

    @Override
    public void setOnShowListener(OnShowListener onShowListener) {
        mAlertDialog.setOnShowListener(dialog -> {
            if (onShowListener != null) onShowListener.onShow(AlertDialogImpl.this);
        });
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        
    }

    @Override
    public void setTitle(CharSequence title) {
        mAlertDialog.setTitle(title);
    }

    @Override
    public void setTitle(int title) {
        mAlertDialog.setTitle(title);
    }

    @Override
    public void setContent(CharSequence content) {
        mAlertDialog.setMessage(content);
    }

    @Override
    public void setContent(int content) {
        setContent(mAlertDialog.getContext().getString(content));
    }

    @Override
    public void setPositiveButton(CharSequence buttonText, OnClickListener listener) {
        mAlertDialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                buttonText,
                (dialog, which) -> {
                    if (listener != null) listener.onClick(AlertDialogImpl.this, which);
                }
        );
    }

    @Override
    public void setPositiveButton(int buttonText, OnClickListener listener) {
        setPositiveButton(mAlertDialog.getContext().getText(buttonText), listener);
    }

    @Override
    public void setNegativeButton(CharSequence buttonText, OnClickListener listener) {
        mAlertDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                buttonText,
                (dialog, which) -> {
                    if (listener != null) listener.onClick(AlertDialogImpl.this, which);
                }
        );
    }

    @Override
    public void setNegativeButton(int buttonText, OnClickListener listener) {
        setNegativeButton(mAlertDialog.getContext().getText(buttonText), listener);
    }
}
