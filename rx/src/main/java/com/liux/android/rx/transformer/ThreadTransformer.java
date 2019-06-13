package com.liux.android.rx.transformer;

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
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
