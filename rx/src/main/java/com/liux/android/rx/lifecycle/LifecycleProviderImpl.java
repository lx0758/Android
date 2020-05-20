package com.liux.android.rx.lifecycle;

import androidx.annotation.NonNull;

import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.RxLifecycle;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * 2018/2/13
 * By Liux
 * lx0758@qq.com
 */

public abstract class LifecycleProviderImpl<E> implements LifecycleProvider<E> {

    private BehaviorSubject<E> mSubject;

    public LifecycleProviderImpl(BehaviorSubject<E> subject) {
        this.mSubject = subject;
    }

    public BehaviorSubject<E> getSubject() {
        return mSubject;
    }

    @NonNull
    @Override
    public Observable<E> lifecycle() {
        return mSubject.hide();
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull E event) {
        return RxLifecycle.bindUntilEvent(mSubject, event);
    }
}
