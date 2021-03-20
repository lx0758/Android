package org.android.framework.ui.authcode;

public class AuthCoder<R> {

    private AuthCodeView authCodeView;
    private AuthCodeRequest.Handler<R> authCodeRequestHandler;
    private AuthCodeResponseHandler<R> authCodeResponseHandler;

    private AuthCodeRequest.Callback<R> authCodeRequestCallback = new AuthCodeRequest.Callback<R>() {
        @Override
        public void onResponse(R r) {
            try {
                String message = authCodeResponseHandler.onResponse(r);
                authCodeView.onSendSucceed(message);
            } catch (Exception e) {
                onFailure(e);
            }
            // 只要有正确的数据返回就检测是否需要图形验证码
            if (authCodeResponseHandler.needRefreshImageCode(r)) {
                authCodeView.onRefreshImageCode();
            }
        }

        @Override
        public void onFailure(Throwable e) {
            String message = authCodeResponseHandler.onFailure(e);
            authCodeView.onSendFailure(message);
        }

        @Override
        public void onCancel() {
            authCodeView.onSendCancel();
        }
    };

    public AuthCoder(AuthCodeView authCodeView, AuthCodeRequest.Handler<R> authCodeRequestHandler, AuthCodeResponseHandler<R> authCodeResponseHandler) {
        this.authCodeView = authCodeView;
        this.authCodeRequestHandler = authCodeRequestHandler;
        this.authCodeResponseHandler = authCodeResponseHandler;
    }

    public void onSend() {
        authCodeView.onSendStart();
        authCodeRequestHandler.onRequest(new AuthCodeRequest<R>(authCodeRequestCallback));
    }

    public void onDestroy() {
        authCodeView.onDestroy();
    }
}
