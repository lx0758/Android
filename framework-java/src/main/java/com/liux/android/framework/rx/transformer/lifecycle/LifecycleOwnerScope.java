package com.liux.android.framework.rx.transformer.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class LifecycleOwnerScope extends Scope implements LifecycleEventObserver {

    private LifecycleOwner lifecycleOwner;
    private Lifecycle.Event event;

    public LifecycleOwnerScope(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        this.event = Lifecycle.Event.ON_DESTROY;
    }

    public LifecycleOwnerScope(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        this.lifecycleOwner = lifecycleOwner;
        this.event = event;
    }

    @Override
    protected void onSubscribe() {
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    protected void onFinally() {
        lifecycleOwner.getLifecycle().removeObserver(this);
        lifecycleOwner = null;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (this.event == event) {
            doFinish();
        }
    }
}
