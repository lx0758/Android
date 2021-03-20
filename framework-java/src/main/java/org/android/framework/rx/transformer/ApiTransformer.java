package org.android.framework.rx.transformer;

import org.android.framework.rx.IResp;
import org.android.framework.rx.exception.NoLoginException;
import org.android.framework.rx.exception.RespException;
import org.android.framework.rx.exception.UpdateException;

import org.reactivestreams.Publisher;

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
 * 2018/2/28
 * By Liux
 * lx0758@qq.com
 */

public class ApiTransformer {

    /**
     * 将 IResp<T> 的数据进行验证, 如果业务代码返回不是"成功", 则抛错
     * @param <T>
     * @return
     */
    public static <T extends IResp> CheckTransformer<T> check() {
        return new CheckTransformer<>();
    }

    /**
     * 将 IResp<T> 的数据进行验证, 并将类型转换为 T
     * @param <T>
     * @return
     */
    public static <T> DataTransformer<T> data() {
        return new DataTransformer<>();
    }

    public static class CheckTransformer<T extends IResp> implements ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {
        @Override
        public CompletableSource apply(Completable upstream) {
            return upstream;
        }

        @Override
        public Publisher<T> apply(Flowable<T> upstream) {
            return upstream
                    .map(new Function<T, T>() {
                        @Override
                        public T apply(T tResp) throws Exception {
                            if (tResp.needLogin()) throw new NoLoginException(tResp);
                            if (tResp.needUpdate()) throw new UpdateException(tResp);
                            if (!tResp.successful()) throw new RespException(tResp);
                            return tResp;
                        }
                    })
                    .compose(ThreadTransformer.io_Main());
        }

        @Override
        public MaybeSource<T> apply(Maybe<T> upstream) {
            return upstream
                    .map(new Function<T, T>() {
                        @Override
                        public T apply(T tResp) throws Exception {
                            if (tResp.needLogin()) throw new NoLoginException(tResp);
                            if (tResp.needUpdate()) throw new UpdateException(tResp);
                            if (!tResp.successful()) throw new RespException(tResp);
                            return tResp;
                        }
                    })
                    .compose(ThreadTransformer.io_Main());
        }

        @Override
        public ObservableSource<T> apply(Observable<T> upstream) {
            return upstream
                    .map(new Function<T, T>() {
                        @Override
                        public T apply(T tResp) throws Exception {
                            if (tResp.needLogin()) throw new NoLoginException(tResp);
                            if (tResp.needUpdate()) throw new UpdateException(tResp);
                            if (!tResp.successful()) throw new RespException(tResp);
                            return tResp;
                        }
                    })
                    .compose(ThreadTransformer.io_Main());
        }

        @Override
        public SingleSource<T> apply(Single<T> upstream) {
            return upstream
                    .map(new Function<T, T>() {
                        @Override
                        public T apply(T tResp) throws Exception {
                            if (tResp.needLogin()) throw new NoLoginException(tResp);
                            if (tResp.needUpdate()) throw new UpdateException(tResp);
                            if (!tResp.successful()) throw new RespException(tResp);
                            return tResp;
                        }
                    })
                    .compose(ThreadTransformer.io_Main());
        }
    }

    public static class DataTransformer<T> implements  ObservableTransformer<IResp<T>, T>, FlowableTransformer<IResp<T>, T>, SingleTransformer<IResp<T>, T>, MaybeTransformer<IResp<T>, T>, CompletableTransformer {
        @Override
        public CompletableSource apply(Completable upstream) {
            return upstream;
        }

        @Override
        public Publisher<T> apply(Flowable<IResp<T>> upstream) {
            return upstream.compose(check()).map(new Function<IResp, T>() {
                @Override
                public T apply(IResp iResp) throws Exception {
                    return (T) iResp.data();
                }
            });
        }

        @Override
        public MaybeSource<T> apply(Maybe<IResp<T>> upstream) {
            return upstream.compose(check()).map(new Function<IResp, T>() {
                @Override
                public T apply(IResp iResp) throws Exception {
                    return (T) iResp.data();
                }
            });
        }

        @Override
        public ObservableSource<T> apply(Observable<IResp<T>> upstream) {
            return upstream.compose(check()).map(new Function<IResp, T>() {
                @Override
                public T apply(IResp iResp) throws Exception {
                    return (T) iResp.data();
                }
            });
        }

        @Override
        public SingleSource<T> apply(Single<IResp<T>> upstream) {
            return upstream.compose(check()).map(new Function<IResp, T>() {
                @Override
                public T apply(IResp iResp) throws Exception {
                    return (T) iResp.data();
                }
            });
        }
    }
}