package com.liux.android.rx.error;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;

/**
 * 2018/2/28
 * By Liux
 * lx0758@qq.com
 */

public class ErrorTransformer {

    private static final ObservableTransformer TRANSFORMER_ERROR = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandlerManager.handler(throwable);
                        }
                    });
        }
    };

    public static <T> ObservableTransformer<T, T> get() {
        return TRANSFORMER_ERROR;
    }
}
