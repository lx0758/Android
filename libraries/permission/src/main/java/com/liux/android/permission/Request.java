package com.liux.android.permission;

import android.app.Activity;

public abstract class Request<T extends Request> {

    protected Activity target;
    protected OnContinueListener onContinueListener;

    protected Request(Activity target) {
        this.target = target;
    }

    public T listener(OnContinueListener listener) {
        this.onContinueListener = listener;
        return (T) this;
    }

    public abstract void request();
}
