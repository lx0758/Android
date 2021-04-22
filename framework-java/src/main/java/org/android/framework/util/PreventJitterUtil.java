package org.android.framework.util;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;

/**
  * date：2019/1/23 21:51
  * author：Liux
  * email：lx0758@qq.com
  * description：防抖动工具
  */
public class PreventJitterUtil {

    public static void onTextChanged(TextView textView, Consumer<String> stringConsumer) {
        onTextChanged(textView, 500, stringConsumer);
    }

    @SuppressLint("CheckResult")
    public static void onTextChanged(TextView textView, long time, Consumer<String> stringConsumer) {
        Observable.create((ObservableOnSubscribe<String>) emitter -> textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emitter.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }))
                .debounce(time, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(stringConsumer);
    }

    public static void onClick(View view, Consumer<View> stringConsumer) {
        onClick(view, 300, stringConsumer);
    }

    @SuppressLint("CheckResult")
    public static void onClick(View view, int time, Consumer<View> stringConsumer) {
        Observable.create((ObservableOnSubscribe<View>) emitter -> view.setOnClickListener(v -> emitter.onNext(v)))
                .throttleFirst(time, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(stringConsumer);
    }
}
