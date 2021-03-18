package com.liux.android.framework.rx.transformer.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.core.BackpressureStrategy;
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
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.core.SingleTransformer;

public class LifecycleTransformer<T> implements ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    /**
     * 绑定生命周期
     * @param lifecycleOwner
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bind(LifecycleOwner lifecycleOwner) {
        return bind(new LifecycleOwnerScope(lifecycleOwner));
    }

    /**
     * 绑定生命周期
     * @param <T>
     * @param lifecycleOwner
     * @param event
     * @return
     */
    public static <T> LifecycleTransformer<T> bind(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        return bind(new LifecycleOwnerScope(lifecycleOwner, event));
    }

    /**
     * 绑定生命周期
     * @param scope
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bind(Scope scope) {
        return new LifecycleTransformer<>(scope);
    }

    private Scope scope;

    public LifecycleTransformer(Scope scope) {
        this.scope = scope;
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream.takeUntil(scope.getObservable().flatMapCompletable(o -> Completable.complete()));
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.takeUntil(scope.getObservable().toFlowable(BackpressureStrategy.LATEST));
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream.takeUntil(scope.getObservable().firstElement());
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.takeUntil(scope.getObservable());
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.takeUntil(scope.getObservable().firstOrError());
    }
}
