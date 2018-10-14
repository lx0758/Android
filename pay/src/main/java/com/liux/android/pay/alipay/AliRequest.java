package com.liux.android.pay.alipay;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.liux.android.pay.PayTool;
import com.liux.android.pay.Request;

public abstract class AliRequest extends Request<String, AliResult> {
    // 订单支付成功
    public static final String ALI_MEMO_SUCCEED = "9000";
    // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
    public static final String ALI_MEMO_UNDERWAY = "8000";
    // 订单支付失败
    public static final String ALI_MEMO_FAILURE = "4000";
    // 重复请求
    public static final String ALI_MEMO_REPEAT = "5000";
    // 用户中途取消
    public static final String ALI_MEMO_CANCEL = "6001";
    // 网络连接出错
    public static final String ALI_MEMO_ERROR = "6002";
    // 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
    public static final String ALI_MEMO_UNKNOWN = "6004";

    private boolean sandbox;

    protected AliRequest(String bill) {
        this(bill, false);
    }

    protected AliRequest(String bill, boolean sandbox) {
        super(bill);
        this.sandbox = sandbox;
        PayTool.println("创建支付宝支付实例:" + bill);
    }

    @Override
    protected void start() {
        PayTool.println("开始支付宝支付:" + bill);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 是否开启沙箱模式
                EnvUtils.setEnv(!sandbox ? EnvUtils.EnvEnum.ONLINE : EnvUtils.EnvEnum.SANDBOX);

                PayTask alipay = new PayTask(activity);
                final String result = alipay.pay(bill, true);
                PayTool.println("支付宝支付结果:" + result);

                PayTool.println("回调支付结果");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AliResult aliResult = new AliResult(result);
                        callback(aliResult);
                    }
                });
            }
        }).start();
    }
}
