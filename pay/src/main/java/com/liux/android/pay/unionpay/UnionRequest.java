package com.liux.android.pay.unionpay;

import android.content.Intent;

import com.liux.android.pay.Payer;
import com.liux.android.pay.Request;
import com.unionpay.UPPayAssistEx;

import java.util.HashMap;
import java.util.Map;

public abstract class UnionRequest extends Request<String, UnionResult> {
    public static final String RESULT_SUCCEED = "success";
    public static final String RESULT_FAILURE = "fail";
    public static final String RESULT_CANCEL = "cancel";
    public static final String RESULT_NOPLUG = "noplug";

    private static Map<String, UnionRequest> WX_REQUESTS = new HashMap<>();

    public static UnionRequest getUnionRequest(String key) {
        UnionRequest unionRequest = WX_REQUESTS.get(key);
        WX_REQUESTS.remove(key);
        return unionRequest;
    }

    public static void putUnionRequest(String key, UnionRequest unionRequest) {
        WX_REQUESTS.put(key, unionRequest);
    }

    protected UnionRequest(String orderInfo) {
        super(orderInfo);
        Payer.println("创建银联支付实例:" + bill);
    }

    @Override
    protected void start() {
        if (!checkConfig()) return;

        Payer.println("开始银联支付:" + bill.toString());
        UnionRequest.putUnionRequest(bill, this);
        Intent intent = new Intent(activity, UnionPayActivity.class);
        intent.putExtra(UnionPayActivity.PARAM_PAY_BILL, bill);
        activity.startActivityForResult(intent, UnionPayActivity.REQUEST_CODE);
    }

    /**
     * 安装银联支付插件
     * 下载地址 <a href="http://mobile.unionpay.com/getclient?platform=android&type=securepayplugin">下载地址</a>
     */
    public void installPlugin() {
        UPPayAssistEx.installUPPayPlugin(activity);
    }

    private boolean checkConfig() {
        Payer.println("银联支付预检查:" + bill);

        if (bill == null || bill.length() < 10) {
            checkFailure("请求参数自检失败,请检查银联支付orderInfo(TN)参数是否正确");
            return false;
        }

        if (!UPPayAssistEx.checkWalletInstalled(activity)) {
            Payer.println("检测到银联支付控件未安装,可以通过链接下载并安装 http://mobile.unionpay.com/getclient?platform=android&type=securepayplugin");
        }

        Payer.println("银联支付预检查完毕:" + bill);
        return true;
    }

    private void checkFailure(String msg) {
        Payer.println("银联支付预检查失败:" + msg);
        Payer.println("终止银联支付:" + bill);
        Payer.println("回调支付结果");
        callback(new UnionResult(RESULT_FAILURE, null));
    }
}