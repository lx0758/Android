package com.liux.android.framework.rx.transformer;

import com.liux.android.framework.base.BaseContract;
import com.liux.android.framework.ui.status.OnCancelListener;
import com.liux.android.framework.ui.status.loading.LoadingView;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

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
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;

public class LoadingTransformer<T> implements  ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    /**
     * 绑定等待弹出框
     * @param view
     * @param <T>
     * @return
     */
    public static <T> LoadingTransformer<T> bind(BaseContract.View view) {
        return bind(view, null, false);
    }

    /**
     * 绑定等待弹出框
     * @param view
     * @param context
     * @param <T>
     * @return
     */
    public static <T> LoadingTransformer<T> bind(BaseContract.View view, String context) {
        return bind(view, context, false);
    }

    /**
     * 绑定等待弹出框
     * @param view
     * @param canCancel
     * @param <T>
     * @return
     */
    public static <T> LoadingTransformer<T> bind(BaseContract.View view, boolean canCancel) {
        return bind(view, null, canCancel);
    }

    /**
     * 绑定等待弹出框
     * @param view
     * @param context
     * @param canCancel
     * @param <T>
     * @return
     */
    public static <T> LoadingTransformer<T> bind(BaseContract.View view, String context, boolean canCancel) {
        return bind(view.getLoadingView(), context, canCancel);
    }

    /**
     * 绑定等待弹出框
     * @param loadingView
     * @param content
     * @param canCancel
     * @param <T>
     * @return
     */
    public static <T> LoadingTransformer<T> bind(LoadingView loadingView, String content, boolean canCancel) {
        return new LoadingTransformer<>(loadingView, content, canCancel);
    }

    private LoadingView loadingView;
    private String content;
    private boolean canCancel;

    public LoadingTransformer(LoadingView loadingView, String content, boolean canCancel) {
        this.loadingView = loadingView;
        this.content = content;
        this.canCancel = canCancel;
    }

    @Override
    public CompletableSource apply(final Completable upstream) {
        return upstream
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(final Disposable disposable) throws Exception {
                        loadingView.show(upstream, content, canCancel, new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                if (!disposable.isDisposed()) disposable.dispose();
                            }
                        });
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        loadingView.hide(upstream);
                    }
                });
    }

    @Override
    public Publisher<T> apply(final Flowable<T> upstream) {
        return upstream
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(final Subscription disposable) throws Exception {
                        loadingView.show(upstream, content, canCancel, new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                disposable.cancel();
                            }
                        });
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        loadingView.hide(upstream);
                    }
                });
    }

    @Override
    public MaybeSource<T> apply(final Maybe<T> upstream) {
        return upstream
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(final Disposable disposable) throws Exception {
                        loadingView.show(upstream, content, canCancel, new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                if (!disposable.isDisposed()) disposable.dispose();
                            }
                        });
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        loadingView.hide(upstream);
                    }
                });
    }

    @Override
    public ObservableSource<T> apply(final Observable<T> upstream) {
        return upstream
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(final Disposable disposable) throws Exception {
                        loadingView.show(upstream, content, canCancel, new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                if (!disposable.isDisposed()) disposable.dispose();
                            }
                        });
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        loadingView.hide(upstream);
                    }
                });
    }

    @Override
    public SingleSource<T> apply(final Single<T> upstream) {
        return upstream
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(final Disposable disposable) throws Exception {
                        loadingView.show(upstream, content, canCancel, new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                if (!disposable.isDisposed()) disposable.dispose();
                            }
                        });
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        loadingView.hide(upstream);
                    }
                });
    }
}
