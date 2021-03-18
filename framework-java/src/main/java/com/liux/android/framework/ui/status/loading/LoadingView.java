package com.liux.android.framework.ui.status.loading;

import com.liux.android.framework.ui.status.OnCancelListener;

/**
 * 2018/5/2
 * By Liux
 * lx0758@qq.com
 */
public interface LoadingView {

    void show(Object tag, boolean canCancel, OnCancelListener listener);

    void show(Object tag, String content, boolean canCancel, OnCancelListener listener);

    void hide(Object tag);
}
