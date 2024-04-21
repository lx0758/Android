package com.liux.android.list.adapter.rule;

import androidx.annotation.LayoutRes;

public abstract class SingleRule<T> extends SuperRule<T> {

    public SingleRule(ViewFactory viewFactory) {
        super(viewFactory);
    }

    public SingleRule(@LayoutRes int itemLayout) {
        super(itemLayout);
    }

    @Override
    public boolean canBindData(T t) {
        return true;
    }
}
