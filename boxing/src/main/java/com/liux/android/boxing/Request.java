package com.liux.android.boxing;

import android.app.Activity;

public abstract class Request<T extends Request> {

    protected Activity target;
    protected OnCancelListener onCancelListener;

    public Request(Activity target) {
        this.target = target;
    }

    public T listener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return (T) this;
    }

    public abstract void start();
}
