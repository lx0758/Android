package com.liux.android.framework.ui.status.loading;

import com.liux.android.framework.ui.status.OnCancelListener;

/**
 * 2018/5/2
 * By Liux
 * lx0758@qq.com
 */
class LoadingTask {

    public static LoadingTask create(String content, boolean canCancel, OnCancelListener listener) {
        return new LoadingTask(content, canCancel, listener);
    }

    private String mContent;
    private boolean mCanCancel;
    private OnCancelListener mOnCancelListener;

    public LoadingTask(String content, boolean canCancel, OnCancelListener onCancelListener) {
        this.mContent = content;
        this.mCanCancel = canCancel;
        this.mOnCancelListener = onCancelListener;
    }

    public String getContent() {
        return mContent;
    }

    public boolean isCanCancel() {
        return mCanCancel;
    }

    public OnCancelListener getListener() {
        return mOnCancelListener;
    }
}
