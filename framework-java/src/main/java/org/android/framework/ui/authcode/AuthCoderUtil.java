package org.android.framework.ui.authcode;

import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.android.framework.rx.IResp;
import org.android.framework.rx.exception.RespException;
import org.android.framework.ui.provider.IToast;

import java.util.Locale;

import retrofit2.HttpException;

public class AuthCoderUtil {

    /**
     * 创建普通验证码发送状态管理器
     * @param iToast
     * @param codeTextView
     * @param handler
     * @return
     */
    public static AuthCoder<IResp> create(IToast iToast, TextView codeTextView, AuthCodeRequest.Handler<IResp> handler) {
        return new AuthCoder<>(
                new DefaultAuthCodeView(iToast, codeTextView),
                handler,
                new DefaultAuthCodeResponseHandler()
        );
    }

    public static class DefaultAuthCodeView extends AuthCodeView {

        IToast iToast;
        TextView codeTextView;

        DefaultAuthCodeView(IToast iToast, TextView codeTextView) {
            this.iToast = iToast;
            this.codeTextView = codeTextView;
        }

        @Override
        protected void onRefreshImageCode() {

        }

        @Override
        protected void onSendStart() {
            codeTextView.setText("发送中");
            codeTextView.setEnabled(false);
        }

        @Override
        public void onSendSucceed(String message) {
            super.onSendSucceed(message);
            iToast.showSuccess(message);
        }

        @Override
        protected void onSendFailure(String message) {
            resetView();
            iToast.showError(message);
        }

        @Override
        protected void onSendCancel() {
            resetView();
        }

        @Override
        protected void onCountdown(int time) {
            codeTextView.setText(String.format(Locale.getDefault(), "%ds", time));
        }

        @Override
        protected void onCountdownEnd() {
            resetView();
        }

        private void resetView() {
            codeTextView.setText("重新发送");
            codeTextView.setEnabled(true);
        }
    }

    private static class DefaultAuthCodeResponseHandler implements AuthCodeResponseHandler<IResp> {

        @Override
        public boolean needRefreshImageCode(IResp resp) {
            if (resp.data() == null) return false;
            return resp.successful();
        }

        @Override
        public String onResponse(IResp booleanIResp) throws Exception {
            if (!booleanIResp.successful()) {
                throw new RespException(booleanIResp);
            }
            return booleanIResp.message();
        }

        @Override
        public String onFailure(Throwable e) {
            String message;
            if (e.getClass().getPackage() != null && e.getClass().getPackage().getName().equals("java.net")) {
                message = "网络连接失败,请检查网络连接";
            } else if (e instanceof HttpException) {
                message = "网络连接失败,请检查网络连接";
            } else if (e instanceof JsonProcessingException) {
                message = "服务器数据解析异常";
            } else if (e instanceof RespException) {
                message = e.getMessage();
            } else {
                message = "未知错误";
            }
            return message;
        }
    }
}
