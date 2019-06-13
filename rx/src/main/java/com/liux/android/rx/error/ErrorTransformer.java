package com.liux.android.rx.error;

import org.reactivestreams.Publisher;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Consumer;

/**
 * 2018/2/28
 * By Liux
 * lx0758@qq.com
 */

public class ErrorTransformer<T> implements  ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    public static <T> ErrorTransformer<T> get() {
        return new ErrorTransformer<T>();
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream.doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandlerManager.handler(throwable);
            }
        });
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandlerManager.handler(throwable);
            }
        });
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream.doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandlerManager.handler(throwable);
            }
        });
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandlerManager.handler(throwable);
            }
        });
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandlerManager.handler(throwable);
            }
        });
    }
}
