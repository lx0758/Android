package com.liux.android.pay;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;

/**
 * Created by Liux on 2017/8/7.
 */

public class PayTool {
    private static final String TAG = "[PayTool]";

    public static boolean DEBUG = false;

    public static PayTool with(Activity activity) {
        return new PayTool(activity);
    }

    public static PayTool with(Fragment fragment) {
        return new PayTool(fragment.getActivity());
    }

    public static PayTool with(android.support.v4.app.Fragment fragment) {
        return new PayTool(fragment.getActivity());
    }

    public static void println(String msg) {
        if (!DEBUG) return;
        Log.d(TAG, ">>>" + msg);
    }

    protected Request request;
    protected Activity activity;

    protected PayTool(Activity activity) {
        println("创建支付请求");
        this.activity = activity;
    }

    public PayTool request(Request request) {
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
