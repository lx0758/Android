package com.liux.android.framework.rx.observer;

import android.text.TextUtils;

import com.liux.android.framework.base.BaseContract;

/**
 * 2018-11-9
 * By Liux
 * lx0758@qq.com
 */

public abstract class ToastObserver<T> extends ApiObserver<T> {

    private BaseContract.View mView;

    public ToastObserver(BaseContract.View view) {
        mView = view;
    }

    @Override
    protected abstract void onSucceed(T t);

    @Override
    protected void onFailure(Throwable e, String msg) {
        if (TextUtils.isEmpty(msg)) return;
        mView.getUIProvider().provideIToast().showError(msg);
    }
}
