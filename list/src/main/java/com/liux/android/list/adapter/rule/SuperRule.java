package com.liux.android.list.adapter.rule;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.android.list.holder.SuperHolder;

/**
 * Created by Liux on 2017/11/30.
 * @param <T> 要处理的类型
 */

public abstract class SuperRule<T> extends Rule<T, SuperHolder> {

    public SuperRule(ViewFactory viewFactory) {
        super(viewFactory);
    }

    public SuperRule(@LayoutRes int itemLayout) {
        super(new SuperViewFactory(itemLayout));
    }

    @Override
    public SuperHolder onCreateHolder(ViewGroup parent, View itemView) {
        return new SuperHolder(itemView);
    }

    private static class SuperViewFactory implements ViewFactory {

        private int itemLayout;

        public SuperViewFactory(@LayoutRes int itemLayout) {
            this.itemLayout = itemLayout;
        }

        @Override
        public View createView(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        }
    }
}
