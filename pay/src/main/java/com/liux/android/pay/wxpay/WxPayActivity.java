package com.liux.android.pay.wxpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * ① 集成此 Activity ,并在清单文件中注册; 注意一定要导出
 * activity android:name=".wxapi.WXPayEntryActivity"
 * android:exported="true"
 *
 * ② 自行实现 Activity, 在 {@link IWXAPIEventHandler#onResp(BaseResp)} 回调 {@link  WxRequest#onResp(BaseResp)}
 */
public abstract class WxPayActivity extends Activity implements IWXAPIEventHandler{

    private IWXAPI mIWXAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (mIWXAPI == null) {
            mIWXAPI = WXAPIFactory.createWXAPI(this, null);
        }

        mIWXAPI.handleIntent(intent, this);

        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        WxRequest.onResp(baseResp);
    }
}