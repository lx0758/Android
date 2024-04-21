package com.liux.android.pay;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;

/**
 * Created by Liux on 2017/8/7.
 */

public class Payer {
    private static final String TAG = "[Payer]";

    public static boolean DEBUG = false;

    public static Payer with(Activity activity) {
        return new Payer(activity);
    }

    public static Payer with(Fragment fragment) {
        return new Payer(fragment.getActivity());
    }

    public static Payer with(androidx.fragment.app.Fragment fragment) {
        return new Payer(fragment.getActivity());
    }

    public static void println(String msg) {
        if (!DEBUG) return;
        Log.d(TAG, ">>>" + msg);
    }

    protected Request request;
    protected Activity activity;

    protected Payer(Activity activity) {
        println("创建支付请求");
        this.activity = activity;
    }

    public Payer request(Request request) {
        println("初始化支付组件");
        request.init(activity);
        this.request = request;
        return this;
    }

    public void pay() {
        if (request == null) throw new NullPointerException("request cannot be empty");
        println("开始处理支付");
        request.start();
    }
}
