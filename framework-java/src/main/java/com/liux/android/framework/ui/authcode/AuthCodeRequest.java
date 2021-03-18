package com.liux.android.framework.ui.authcode;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;

public class AuthCodeRequest<R> {

    /**
     * 发送结果回调
     */
    private Callback<R> callback;

    public AuthCodeRequest(Callback<R> callback) {
        this.callback = callback;
    }

    public Callback<R> getCallback() {
        return callback;
    }

    public Action doOnDispose() {
        return new Action() {
            @Override
            public void run() throws Exception {
                callback.onCancel();
            }
        };
    }

    public static abstract class Callback<R> implements SingleObserver<R> {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onSuccess(R r) {
            onResponse(r);
        }

        @Override
        public void onError(Throwable e) {
            onFailure(e);
        }

        public abstract void onResponse(R r);

        public abstract void onFailure(Throwable e);

        public abstract void onCancel();
    }

    /**
     * 验证码请求处理器
     * @param <R>
     */
    public interface Handler<R> {

        void onRequest(AuthCodeRequest<R> authCodeRequest);
    }
}
