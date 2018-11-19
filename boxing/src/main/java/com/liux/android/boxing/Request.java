package com.liux.android.boxing;

import android.app.Activity;

public abstract class Request {

    protected Activity target;

    public Request(Activity target) {
        this.target = target;
    }

    public abstract void start();
}
