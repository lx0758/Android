package com.liux.android.framework.rx.observer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liux.android.framework.rx.IResp;
import com.liux.android.framework.rx.exception.RespException;

import org.reactivestreams.Subscription;

import java.util.Locale;
import java.util.concurrent.CancellationException;

import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 2018/2/28
 * By Liux
 * lx0758@qq.com
 */

public abstract class ApiObserver<T> implements Observer<T>, SingleObserver<T>, MaybeObserver<T>, FlowableSubscriber<T>, CompletableObserver {

    private static ErrorHandler globalErrorHandler;

    public static ErrorHandler getGlobalErrorHandler() {
        return globalErrorHandler;
    }

    public static void setGlobalErrorHandler(ErrorHandler errorHandler) {
        globalErrorHandler = errorHandler;
    }

    private boolean onNextCall = false;
    private Disposable disposable;
    private Subscription subscription;

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public final void onNext(T t) {
        onNextCall = true;
        onSucceed(t);
    }

    @Override
    public final void onError(Throwable e) {
        if (e instanceof CancellationException) return;
        if (globalErrorHandler != null) {
            if (globalErrorHandler.onError(e)) return;
        }
        String message;
        if (e.getClass().getPackage() != null && e.getClass().getPackage().getName().equals("java.net")) {
            message = "网络连接失败,请检查网络连接";
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            message = String.format(Locale.CHINA, "网络连接失败,请检查网络连接\ncode:%d\nmessage:%s", httpException.code(), httpException.message());
        } else if (e instanceof JsonProcessingException) {
            message = "服务器数据解析异常";
        } else if (e instanceof RespException){
            IResp<T> tIResp = ((RespException) e).getResp();
            if (onBusinessFailure(tIResp)) return;
            message = tIResp.message();
        } else {
            message = e.getMessage();
        }
        onFailure(e, message);
    }

    @Override
    public void onComplete() {
        if (!onNextCall) onSucceed(null);
    }

    @Override
    public void onSuccess(T t) {
        onSucceed(t);
    }

    @Override
    public void onSubscribe(Subscription s) {
        subscription = s;
    }

    public void cancel() {
        if (disposable != null) disposable.dispose();
        if (subscription != null) subscription.cancel();
    }

    /**
     * 成功访问到数据
     * @param t
     */
    protected abstract void onSucceed(T t);

    /**
     * 出现错误
     * @param e
     * @param message
     */
    protected abstract void onFailure(Throwable e, String message);

    /**
     * 业务线错误
     * @param tIResp
     * @return 是否已经处理
     */
    protected boolean onBusinessFailure(IResp<T> tIResp) {
        if (tIResp.needLogin() || tIResp.needUpdate()) return true;
        return false;
    }

    public interface ErrorHandler {

        boolean onError(Throwable e);
    }
}
