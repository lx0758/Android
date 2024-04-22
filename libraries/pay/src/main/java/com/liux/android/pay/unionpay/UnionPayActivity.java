package com.liux.android.pay.unionpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.liux.android.pay.Payer;
import com.unionpay.UPPayAssistEx;

/**
 * 银联支付业务调用Activity
 * Created by Liux on 2017/10/24.
 */

public class UnionPayActivity extends Activity {
    protected static final int REQUEST_CODE = 1000;

    protected static final String PARAM_PAY_BILL = "UNION_PAY_BILL";
    protected static final String RESULT_KEY_STATE = "pay_result";
    protected static final String RESULT_KEY_DATA = "result_data";
    protected static final String MODE_PAY_TEST = "01";
    protected static final String MODE_PAY_PRODUCT = "00";

    private String mOrderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startUnionPay(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startUnionPay(intent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOrderInfo = savedInstanceState.getString(PARAM_PAY_BILL);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAM_PAY_BILL, mOrderInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        String state = data.getExtras().getString(RESULT_KEY_STATE);
        if (state == null) {
            return;
        }
        switch (state) {
            case UnionRequest.RESULT_SUCCEED:
                String result_data = null;
                if (data.hasExtra(RESULT_KEY_DATA)) {
                    result_data = data.getExtras().getString(RESULT_KEY_DATA);
                }
                callCallback(UnionRequest.RESULT_SUCCEED, result_data);
                break;
            case UnionRequest.RESULT_FAILURE:
                callCallback(UnionRequest.RESULT_FAILURE, null);
                break;
            case UnionRequest.RESULT_CANCEL:
                callCallback(UnionRequest.RESULT_CANCEL, null);
                break;
            case UnionRequest.RESULT_NOPLUG:
                callCallback(UnionRequest.RESULT_NOPLUG, null);
                break;
        }
    }

    private void startUnionPay(Intent intent) {
        mOrderInfo = intent.getStringExtra(PARAM_PAY_BILL);
        int result = callUnionPay(this, null, null, mOrderInfo, MODE_PAY_PRODUCT);
        if (result != UPPayAssistEx.PLUGIN_VALID) {
            callCallback(UnionRequest.RESULT_NOPLUG, null);
        }
    }

    /**
     * 启动支付控件的接口
     * @param context 用于启动支付控件的活动对象
     * @param spId 保留使用，这里输入null
     * @param sysProvider 保留使用，这里输入null
     * @param orderInfo 订单信息为交易流水号，即TN，为商户后台从银联后台获取
     * @param mode 银联后台环境标识，“00”将在银联正式环境发起交易,“01”将在银联测试环境发起交易
     * @return
     */
    private int callUnionPay(Context context, String spId, String sysProvider, String orderInfo, String mode) {
        return UPPayAssistEx.startPay(context, spId, sysProvider, orderInfo, mode);
    }

    private void callCallback(String result, String data) {
        Payer.println("银联支付结果:" + result);

        Payer.println("回调支付结果");
        UnionRequest unionRequest = UnionRequest.getUnionRequest(mOrderInfo);
        if (unionRequest != null) {
            unionRequest.callback(new UnionResult(result, data));
        }

        finish();
    }
}
