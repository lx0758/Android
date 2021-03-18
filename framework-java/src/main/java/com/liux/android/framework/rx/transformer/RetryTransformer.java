package com.liux.android.framework.rx.transformer;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

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
import io.reactivex.rxjava3.functions.Function;

/**
 * date：2019/1/30 17:04
 * author：Liux
 * email：lx0758@qq.com
 * description：扩展转换器
 */

public class RetryTransformer<T> implements ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    /**
     * 重试
     * @param maxRetries 最多次数
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> retry(int maxRetries) {
        return new RetryTransformer<>(maxRetries, 0);
    }

    /**
     * 延迟重试
     * @param maxRetries 最多次数
     * @param delayMillis 间隔时间
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> delayRetry(int maxRetries, int delayMillis) {
        return new RetryTransformer<>(maxRetries, delayMillis);
    }

    private int maxRetries;
    private int delayMillis;

    public RetryTransformer(int maxRetries, int delayMillis) {
        this.maxRetries = maxRetries;
        this.delayMillis = delayMillis;
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream.retryWhen(new Function<Flowable<Throwable>, Publisher<?>>() {
            private int retryCount;
            @Override
            public Publisher<?> apply(Flowable<Throwable> throwableFlowable) throws Exception {
                return throwableFlowable.flatMap(new Function<Throwable, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Throwable throwable) throws Exception {
                        if (++retryCount <= maxRetries) {
                            return Flowable.timer(delayMillis, TimeUnit.MILLISECONDS);
                        }
                        // Max retries hit. Just pass the error along.
                        return Flowable.error(throwable);
                    }
                });
            }
        });
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.retryWhen(new Function<Flowable<Throwable>, Publisher<?>>() {
            private int retryCount;
            @Override
            public Publisher<?> apply(Flowable<Throwable> throwableFlowable) throws Exception {
                return throwableFlowable.flatMap(new Function<Throwable, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Throwable throwable) throws Exception {
                        if (++retryCount <= maxRetries) {
                            return Flowable.timer(delayMillis, TimeUnit.MILLISECONDS);
                        }
                        // Max retries hit. Just pass the error along.
                        return Flowable.error(throwable);
                    }
                });
            }
        });
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream.retryWhen(new Function<Flowable<Throwable>, Publisher<?>>() {
            private int retryCount;
            @Override
            public Publisher<?> apply(Flowable<Throwable> throwableFlowable) throws Exception {
                return throwableFlowable.flatMap(new Function<Throwable, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Throwable throwable) throws Exception {
                        if (++retryCount <= maxRetries) {
                            return Flowable.timer(delayMillis, TimeUnit.MILLISECONDS);
                        }
                        // Max retries hit. Just pass the error along.
                        return Flowable.error(throwable);
                    }
                });
            }
        });
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            private int retryCount;
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> apply(Throwable throwable) throws Exception {
                        if (++retryCount <= maxRetries) {
                            return Observable.timer(delayMillis, TimeUnit.MILLISECONDS);
                        }
                        // Max retries hit. Just pass the error along.
                        return Observable.error(throwable);
                    }
                });
            }
        });
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.retryWhen(new Function<Flowable<Throwable>, Publisher<?>>() {
            private int retryCount;
            @Override
            public Publisher<?> apply(Flowable<Throwable> throwableFlowable) throws Exception {
                return throwableFlowable.flatMap(new Function<Throwable, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Throwable throwable) throws Exception {
                        if (++retryCount <= maxRetries) {
                            return Flowable.timer(delayMillis, TimeUnit.MILLISECONDS);
                        }
                        // Max retries hit. Just pass the error along.
                        return Flowable.error(throwable);
                    }
                });
            }
        });
    }
}
