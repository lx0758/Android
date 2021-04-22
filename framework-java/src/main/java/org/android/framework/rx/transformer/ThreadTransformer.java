package org.android.framework.rx.transformer;

import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.CompletableTransformer;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeSource;
import io.reactivex.rxjava3.core.MaybeTransformer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.core.SingleTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by Liux on 2018/2/14.
 */

public class ThreadTransformer<T> implements ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    public static <T> ThreadTransformer<T> single_Main() {
        return new ThreadTransformer<>(Schedulers.single(), AndroidSchedulers.mainThread());
    }

    public static <T> ThreadTransformer<T> computation_Main() {
        return new ThreadTransformer<>(Schedulers.computation(), AndroidSchedulers.mainThread());
    }

    public static <T> ThreadTransformer<T> io_Main() {
        return new ThreadTransformer<>(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    public static <T> ThreadTransformer<T> trampoline_Main() {
        return new ThreadTransformer<>(Schedulers.trampoline(), AndroidSchedulers.mainThread());
    }

    public static <T> ThreadTransformer<T> newThread_Main() {
        return new ThreadTransformer<>(Schedulers.newThread(), AndroidSchedulers.mainThread());
    }

    public static <T> ThreadTransformer<T> customize(Scheduler subscribe, Scheduler observe) {
        return new ThreadTransformer<>(subscribe, observe);
    }

    private Scheduler subscribe;
    private Scheduler observe;

    public ThreadTransformer(Scheduler subscribe, Scheduler observe) {
        this.subscribe = subscribe;
        this.observe = observe;
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream
                .subscribeOn(subscribe)
                .observeOn(observe);
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream
                .subscribeOn(subscribe)
                .observeOn(observe);
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream
                .subscribeOn(subscribe)
                .observeOn(observe);
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .subscribeOn(subscribe)
                .observeOn(observe);
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream
                .subscribeOn(subscribe)
                .observeOn(observe);
    }
}
