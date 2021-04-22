package com.liux.android.pay;

import android.app.Activity;

public abstract class Request<T, R> {
    protected T bill;
    protected Activity activity;

    /**
     * 初始化支付实例并传入支付参数
     * @param bill
     */
    protected Request(T bill) {
        this.bill = bill;
    }

    /**
     * 绑定Activity用于初始化
     * @param activity
     */
    protected void init(Activity activity) {
        this.activity = activity;
    }

    /**
     * 开始支付操作
     */
    protected abstract void start();

    /**
     * 支付操作回调
     * @param r
     */
    public abstract void callback(R r);
}
