package com.liux.android.rx.transformer;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liux on 2018/2/14.
 */

public class ThreadTransformer {

    private static final ObservableTransformer TRANSFORMER_SINGLE_MAIN = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream
                    .subscribeOn(Schedulers.single())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer TRANSFORMER_COMPUTATION_MAIN = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer TRANSFORMER_IO_MAIN = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer TRANSFORMER_TRAMPOLINE_MAIN = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer TRANSFORMER_NEWTHREAD_MAIN = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> single_Main() {
        return TRANSFORMER_SINGLE_MAIN;
    }

    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> computation_Main() {
        return TRANSFORMER_COMPUTATION_MAIN;
    }

    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> io_Main() {
        return TRANSFORMER_IO_MAIN;
    }

    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> trampoline_Main() {
        return TRANSFORMER_TRAMPOLINE_MAIN;
    }

    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> newThread_Main() {
        return TRANSFORMER_NEWTHREAD_MAIN;
    }
}
