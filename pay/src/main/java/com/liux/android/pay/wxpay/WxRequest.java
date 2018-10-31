package com.liux.android.pay.wxpay;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import com.liux.android.pay.Payer;
import com.liux.android.pay.Request;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 可能出现的问题,转跳支付界面时App被杀死,造成微信无法回调
 */
public abstract class WxRequest extends Request<PayReq, PayResp> {
    private static Map<String, WxRequest> WX_REQUESTS = new HashMap<>();

    public static WxRequest getWxRequest(String key) {
        WxRequest wxRequest = WX_REQUESTS.get(key);
        WX_REQUESTS.remove(key);
        return wxRequest;
    }

    public static void putWxRequest(String key, WxRequest wxRequest) {
        WX_REQUESTS.put(key, wxRequest);
    }

    public static void onResp(BaseResp baseResp) {
        if (baseResp.getType() != ConstantsAPI.COMMAND_PAY_BY_WX) return;
        PayResp payResp = (PayResp) baseResp;

        Payer.println("微信支付结果:" + "[prepayId=" + payResp.prepayId + ",errCode=" + baseResp.errCode + "]");

        String key = payResp.prepayId;
        WxRequest wxPay = WxRequest.getWxRequest(key);
        if (wxPay != null) {
            Payer.println("回调支付结果");
            wxPay.callback(payResp);
        }
    }

    public static final int ERR_PARAM = -101;
    public static final int ERR_CONFIG = -102;
    public static final int ERR_VERSION = -103;

    private IWXAPI mIWXAPI;

    protected WxRequest(PayReq bill) {
        super(bill);
        Payer.println("创建微信支付实例:" + getBillString());
    }

    @Override
    protected void init(Activity activity) {
        super.init(activity);
        mIWXAPI = WXAPIFactory.createWXAPI(activity, null);
        boolean succeed = mIWXAPI.registerApp(bill.appId);
        Payer.println("初始化微信支付实例:" + "[" + succeed + "]");
    }

    @Override
    protected void start() {
        if (!checkConfig()) return;

        Payer.println("开始微信支付:" + getBillString());
        String key = bill.prepayId;
        putWxRequest(key, this);
        mIWXAPI.sendReq(bill);
    }

    private boolean checkConfig() {
        Payer.println("微信支付预检查:" + getBillString());

        String WX_ACTIVITY = activity.getPackageName() + ".wxapi.WXPayEntryActivity";

        // 检验支付参数
        if (bill == null || !bill.checkArgs()) {
            checkFailure(ERR_PARAM, "请求参数自检失败,请检查微信支付请求参数是否正确");
            return false;
        }

        // 检验 WXPayEntryActivity 类
        Object object;
        try {
            object = Class.forName(WX_ACTIVITY).newInstance();
            if (!(object instanceof Activity)) {
                checkFailure(ERR_CONFIG, "类 [" + WX_ACTIVITY + "] 未继承于 [android.app.Activity]");
                return false;
            }
        } catch (ClassNotFoundException e) {
            checkFailure(ERR_CONFIG, "未检测到类 [" + WX_ACTIVITY + "] (注意检查 applicationId 和该类所在包名是否一致, 微信回调以 applicationId 为准)");
            return false;
        } catch (IllegalAccessException e) {
            checkFailure(ERR_CONFIG, "检测到类 [" + WX_ACTIVITY + "] 不能被实例化, 请检查该类的权限修饰符和构造函数权限修饰符是否正确");
            return false;
        } catch (InstantiationException e) {
            checkFailure(ERR_CONFIG, "检测到类 [" + WX_ACTIVITY + "] 不能被实例化, 请检查该类的权限修饰符和构造函数权限修饰符是否正确");
            return false;
        }

        // 检验是否继承类
        if (!(object instanceof WxPayActivity)) {
            Payer.println("[警告]检测到类 [" + WX_ACTIVITY + "] 未继承于 [" + WxPayActivity.class.getPackage().getName() + ".WxPayActivity], 请注意在 [com.tencent.mm.opensdk.openapi.IWXAPIEventHandler.onResp(BaseResp)] 回调方法中静态调用 [" + WxRequest.class.getPackage().getName() + ".WxRequest.onResp(BaseResp)] 方法");
        }

        // 检验清单描述文件
        try {
            boolean find = false;
            ActivityInfo[] infos = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES).activities;
            for (ActivityInfo info : infos) {
                if (WX_ACTIVITY.equals(info.name) && info.exported) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                checkFailure(ERR_CONFIG, "清单文件未注册或者未导出 " + WX_ACTIVITY);
                return false;
            }
        } catch (PackageManager.NameNotFoundException ignore) {}

        // 检验客户端
        if (mIWXAPI.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
            checkFailure(ERR_VERSION, "未安装微信或版本过低");
            return false;
        }

        Payer.println("微信支付预检查完毕:" + getBillString());
        return true;
    }

    private void checkFailure(int code, String msg) {
        PayResp resp = new PayResp();
        resp.errCode = code;
        resp.prepayId = bill.prepayId;
        Payer.println("微信支付预检查失败:" + msg);
        Payer.println("终止微信支付:" + getBillString());
        Payer.println("回调支付结果");
        callback(resp);
    }

    private String getBillString() {
        return String.format("appid=%s,partnerid=%s,prepayid=%s,package=%s,noncestr=%s,timestamp=%s,sign=%s", bill.appId, bill.partnerId, bill.prepayId, bill.packageValue, bill.nonceStr, bill.timeStamp, bill.sign);
    }
}
