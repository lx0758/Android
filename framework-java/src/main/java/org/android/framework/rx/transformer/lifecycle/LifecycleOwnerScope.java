package org.android.framework.rx.transformer.lifecycle;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class LifecycleOwnerScope extends Scope implements LifecycleEventObserver {

    private LifecycleOwner lifecycleOwner;
    private Lifecycle.Event event;
    private Handler handler = new Handler(Looper.getMainLooper());

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
        handler.post(() -> {
            lifecycleOwner.getLifecycle().addObserver(this);
        });
    }

    @Override
    protected void onFinally() {
        handler.post(() -> {
            if (lifecycleOwner != null) {
                lifecycleOwner.getLifecycle().removeObserver(this);
                lifecycleOwner = null;
            }
        });
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (this.event == event) {
            doFinish();
        }
    }
}
