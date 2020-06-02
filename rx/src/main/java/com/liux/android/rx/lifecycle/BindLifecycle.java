package com.liux.android.rx.lifecycle;

import com.trello.rxlifecycle3.LifecycleTransformer;

/**
 * 2018/2/13
 * By Liux
 * lx0758@qq.com
 */

public interface BindLifecycle {

    <T> LifecycleTransformer<T> bindLifecycle();

    <T> LifecycleTransformer<T> bindLifecycle(Event event);
}
