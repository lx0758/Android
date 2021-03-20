package org.android.framework.rx.transformer;

import org.android.framework.base.BaseContract;
import org.android.framework.rx.IResp;
import org.android.framework.rx.exception.EmptyDataException;
import org.android.framework.rx.exception.NoLoginException;
import org.android.framework.ui.paging.PagingRequest;
import org.android.framework.ui.status.OnCancelListener;
import org.android.framework.ui.status.OnRetryListener;
import org.android.framework.ui.status.Status;
import org.android.framework.ui.status.StatusView;

import java.util.Collection;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

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
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.core.SingleTransformer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * 2018/5/2
 * By Liux
 * lx0758@qq.com
 */
public class StatusTransformer<T> implements ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    /**
     * 将界面状态绑定到请求过程
     * @param view
     * @param retryCallback
     * @param <T>
     * @return
     */
    public static <T> StatusTransformer<T> bind(BaseContract.View view, RetryCallback retryCallback) {
        return new Builder(view.getStatusView())
                .retry(retryCallback)
                .build();
    }

    /**
     * 将界面状态绑定到请求过程
     * @param view
     * @param retryCallback
     * @param <T>
     * @return
     */
    public static <T> StatusTransformer<T> bind(BaseContract.View view, RetryCallback retryCallback, LoadingRule loadingRule) {
        return new Builder(view.getStatusView())
                .retry(retryCallback)
                .loadingRule(loadingRule)
                .build();
    }

    /**
     * 将界面状态绑定到请求过程
     * @param view
     * @param retryCallback
     * @param <T>
     * @return
     */
    public static <T> StatusTransformer<T> bind(BaseContract.View view, RetryCallback retryCallback, PagingRequest pagingRequest) {
        return new Builder(view.getStatusView())
                .retry(retryCallback)
                .loadingRule(new LoadingRule() {
                    @Override
                    public boolean needLoading(Status status) {
                        return pagingRequest.isFirst() && pagingRequest.isEmpty();
                    }

                    @Override
                    public boolean needLoadingDialog(Status status) {
                        return false;
                    }
                })
                .checkEmpty(pagingRequest.isFirst())
                .build();
    }

    private Builder builder;

    public StatusTransformer(Builder builder) {
        this.builder = builder;
    }

    @Override
    public CompletableSource apply(final Completable upstream) {
        return upstream
                .doOnSubscribe(doOnSubscribeConsumer(upstream))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> builder.statusView.normal(upstream))
                .doOnError(doOnErrorConsumer(upstream));
    }

    @Override
    public Publisher<T> apply(final Flowable<T> upstream) {
        return upstream
                .doOnSubscribe(doOnSubscribeSubscription(upstream))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(t -> {
                    if (builder.checkEmpty) checkEmptyCollection(t);
                })
                .doOnComplete(() -> builder.statusView.normal(upstream))
                .doOnError(doOnErrorConsumer(upstream));
    }

    @Override
    public MaybeSource<T> apply(final Maybe<T> upstream) {
        return upstream
                .doOnSubscribe(doOnSubscribeConsumer(upstream))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(t -> {
                    if (builder.checkEmpty) checkEmptyCollection(t);
                })
                .doOnComplete(() -> builder.statusView.normal(upstream))
                .doOnError(doOnErrorConsumer(upstream));
    }

    @Override
    public ObservableSource<T> apply(final Observable<T> upstream) {
        return upstream
                .doOnSubscribe(doOnSubscribeConsumer(upstream))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(t -> {
                    if (builder.checkEmpty) checkEmptyCollection(t);
                })
                .doOnComplete(() -> builder.statusView.normal(upstream))
                .doOnError(doOnErrorConsumer(upstream));
    }

    @Override
    public SingleSource<T> apply(final Single<T> upstream) {
        return upstream
                .doOnSubscribe(doOnSubscribeConsumer(upstream))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(t -> {
                    if (builder.checkEmpty) checkEmptyCollection(t);
                    builder.statusView.normal(upstream);
                })
                .doOnError(doOnErrorConsumer(upstream));
    }

    /**
     * 重试回调
     */
    public interface RetryCallback {

        void onRetryCall();
    }

    public static class Builder {

        private StatusView statusView;
        private RetryCallback retryCallback;
        private LoadingRule loadingRule = LoadingRule.Default;
        private boolean checkEmpty = true;

        public Builder(StatusView statusView) {
            this.statusView = statusView;
        }

        public Builder retry(RetryCallback retryCallback) {
            this.retryCallback = retryCallback;
            return this;
        }

        public Builder loadingRule(LoadingRule loadingRule) {
            this.loadingRule = loadingRule;
            return this;
        }

        public Builder checkEmpty(boolean checkEmpty) {
            this.checkEmpty = checkEmpty;
            return this;
        }

        public <T> StatusTransformer<T> build() {
            return new StatusTransformer<>(this);
        }
    }

    public interface LoadingRule {

        LoadingRule Default = new LoadingRule() {
            @Override
            public boolean needLoading(Status status) {
                return true;
            }

            @Override
            public boolean needLoadingDialog(Status status) {
                return status == Status.NORMAL;
            }
        };

        boolean needLoading(Status status);

        boolean needLoadingDialog(Status status);
    }

    /**
     * 订阅后需要做的动作
     * @param tag
     */
    private Consumer<Disposable> doOnSubscribeConsumer(Object tag) {
        return new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (!builder.loadingRule.needLoading(builder.statusView.status())) return;
                builder.statusView.loading(tag, new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        if (!disposable.isDisposed()) disposable.dispose();
                    }
                }, builder.loadingRule.needLoadingDialog(builder.statusView.status()));
            }
        };
    }

    /**
     * 订阅后需要做的动作
     * @param tag
     */
    private Consumer<Subscription> doOnSubscribeSubscription(Object tag) {
        return new Consumer<Subscription>() {
            @Override
            public void accept(Subscription subscription) throws Exception {
                if (!builder.loadingRule.needLoading(builder.statusView.status())) return;
                builder.statusView.loading(tag, new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        subscription.cancel();
                    }
                }, builder.loadingRule.needLoadingDialog(builder.statusView.status()));
            }
        };
    }

    /**
     * 检查 IResp 和 Collection 是否为空对象, IResp.data 为 Collection 时生效
     * @param t
     * @param <T>
     * @throws EmptyDataException
     */
    private static <T> void checkEmptyCollection(T t) throws EmptyDataException {
        if (t == null) return;
        if (t instanceof IResp) {
            checkEmptyCollection(((IResp) t).data());
        } else if (t instanceof Collection) {
            if (((Collection) t).isEmpty()) {
                throw new EmptyDataException();
            }
        }
    }

    /**
     * 错误处理
     * @return
     */
    private Consumer<Throwable> doOnErrorConsumer(Object upstream) {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (throwable instanceof EmptyDataException) {
                    // 检查是否是无数据异常, 这个异常由 checkEmptyCollection 方法抛出
                    builder.statusView.noData(upstream, new OnRetryListener() {
                        @Override
                        public void onRetry() {
                            if (builder.retryCallback != null) builder.retryCallback.onRetryCall();
                        }
                    });
                } else if (throwable instanceof NoLoginException) {
                    // 检查是否为未登录异常
                    builder.statusView.noLogin(upstream, new OnRetryListener() {
                        @Override
                        public void onRetry() {
                            if (builder.retryCallback != null) builder.retryCallback.onRetryCall();
                        }
                    });
                } else if ("java.net".startsWith(throwable.getClass().getName())) {
                    // 检查是否是网络故障异常
                    builder.statusView.noNetwork(upstream, new OnRetryListener() {
                        @Override
                        public void onRetry() {
                            if (builder.retryCallback != null) builder.retryCallback.onRetryCall();
                        }
                    });
                } else {
                    builder.statusView.error(upstream, new OnRetryListener() {
                        @Override
                        public void onRetry() {
                            if (builder.retryCallback != null) builder.retryCallback.onRetryCall();
                        }
                    });
                }
            }
        };
    }
}
