package org.android.framework.rx.transformer.lifecycle;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public abstract class Scope {

    private BehaviorSubject<Object> mBehaviorSubject = BehaviorSubject.create();

    protected Observable<Object> getObservable() {
        return mBehaviorSubject
                .doOnSubscribe(disposable -> onSubscribe())
                .doFinally(this::onFinally);
    }

    protected void doFinish() {
        mBehaviorSubject.onNext(this);
    }

    protected abstract void onSubscribe();

    protected abstract void onFinally();
}
