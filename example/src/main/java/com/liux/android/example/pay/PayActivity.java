package com.liux.android.example.pay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liux.android.example.R;
import com.liux.android.pay.Payer;
import com.liux.android.pay.alipay.AliRequest;
import com.liux.android.pay.alipay.AliResult;
import com.liux.android.pay.unionpay.UnionRequest;
import com.liux.android.pay.unionpay.UnionResult;
import com.liux.android.pay.wxpay.WxRequest;
import com.liux.android.tool.TT;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/11/28.
 */

public class PayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        Payer.DEBUG = true;
    }

    @OnClick({R.id.btn_ali, R.id.btn_wx, R.id.btn_union})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ali:
                Payer.with(this)
                        .request(new AliRequest("") {
                            @Override
                            public void callback(AliResult aliResult) {
                                String result = aliResult.getResultStatus();
                                switch (result) {
                                    case ALI_MEMO_SUCCEED:
                                        makeText("支付成功");
                                        break;
                                    case ALI_MEMO_UNDERWAY:
                                        makeText("支付处理中");
                                        break;
                                    case ALI_MEMO_FAILURE:
                                        makeText("支付失败");
                                        break;
                                    case ALI_MEMO_CANCEL:
                                        makeText("取消支付");
                                        break;
                                    case ALI_MEMO_REPEAT:
                                        makeText("重复支付");
                                        break;
                                    case ALI_MEMO_ERROR:
                                        makeText("网络错误");
                                        break;
                                    case ALI_MEMO_UNKNOWN:
                                        makeText("支付状态未知");
                                        break;
                                    default:
                                        makeText("支付错误 [" + result + "]");
                                        break;
                                }
                            }
                        })
                        .pay();
                break;
            case R.id.btn_wx:
                PayReq payReq = new PayReq();
                payReq.extData = "附加数据可以随便写";

                Payer.with(this)
                        .request(new WxRequest(payReq) {
                            @Override
                            public void callback(PayResp payResp) {
                                // 这里的附加数据是等于请求中在附加数据的,并且不参与验签
                                String extData = payResp.extData;

                                switch (payResp.errCode) {
                                    case ERR_PARAM:
                                        makeText("参数错误");
                                        break;
                                    case ERR_CONFIG:
                                        makeText("配置错误");
                                        break;
                                    case ERR_VERSION:
                                        makeText("微信客户端未安装或版本过低");
                                        break;
                                    case PayResp.ErrCode.ERR_OK:
                                        makeText("支付成功");
                                        break;
                                    case PayResp.ErrCode.ERR_COMM:
                                        makeText("支付错误");
                                        break;
                                    case PayResp.ErrCode.ERR_USER_CANCEL:
                                        makeText("取消支付");
                                        break;
                                    case PayResp.ErrCode.ERR_SENT_FAILED:
                                    case PayResp.ErrCode.ERR_AUTH_DENIED:
                                    case PayResp.ErrCode.ERR_UNSUPPORT:
                                    case PayResp.ErrCode.ERR_BAN:
                                    default:
                                        makeText("支付错误 [" + payResp.errStr + "]");
                                        break;
                                }
                            }
                        })
                        .pay();
                break;
            case R.id.btn_union:
                Payer.with(this)
                        .request(new UnionRequest("") {
                            @Override
                            public void callback(UnionResult unionResult) {
                                String result = unionResult.getResult();
                                switch (result) {
                                    case RESULT_SUCCEED:
                                        String data = unionResult.getData();
                                        makeText("支付成功");
                                        break;
                                    case RESULT_FAILURE:
                                        makeText("支付失败");
                                        break;
                                    case RESULT_CANCEL:
                                        makeText("取消支付");
                                        break;
                                    case RESULT_NOPLUG:
                                        makeText("未安装银联支付控件");
                                        break;
                                }
                            }
                        })
                        .pay();
                break;
        }
    }

    private void makeText(String msg) {
        TT.makeText(this, msg, TT.LENGTH_SHORT).show();
    }
}
