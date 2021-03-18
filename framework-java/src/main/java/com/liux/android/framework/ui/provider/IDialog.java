package com.liux.android.framework.ui.provider;

/**
 * 2018-3-8
 * By Liux
 * lx0758@qq.com
 */

public interface IDialog {

    boolean isShowing();

    void show();

    void dismiss();

    void cancel();

    void setCancelable(boolean cancelable);

    void setOnCancelListener(OnCancelListener onCancelListener);

    void setOnDismissListener(OnDismissListener onDismissListener);

    void setOnShowListener(OnShowListener onShowListener);

    void setOnClickListener(OnClickListener onClickListener);

    interface OnCancelListener {

        void onCancel(IDialog dialog);
    }

    interface OnDismissListener {

        void onDismiss(IDialog dialog);
    }

    interface OnShowListener {

        void onShow(IDialog dialog);
    }

    interface OnClickListener {

        void onClick(IDialog dialog, int which);
    }
}
