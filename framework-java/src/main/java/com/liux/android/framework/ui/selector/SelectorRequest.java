package com.liux.android.framework.ui.selector;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

public class SelectorRequest {

    private LifecycleOwner lifecycleOwner;

    public SelectorRequest(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    public interface Handler {

        void request(SelectorRequest selectorRequest, int level, SelectorBean lastSelectBean, Callback callback);
    }

    public interface Callback {

        void onShow(List<? extends SelectorBean> selectorBeanList);

        void onComplete();
    }
}
