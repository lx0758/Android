package com.liux.android.list.adapter.rule;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.liux.android.list.adapter.state.State;

/**
 * Bean 和 View 绑定显示规则
 * @param <T> 要处理的类型
 */
public abstract class Rule<T, VH extends RecyclerView.ViewHolder> {

    private ViewFactory viewFactory;

    public Rule(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }

    /**
     * 判断数据是否匹配规则
     * @param object
     * @return
     */
    public boolean canBindObject(Object object) {
        try {
            T t = (T) object;
            return canBindData(t);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * 数据类型相同,是否匹配规则
     * @param t
     * @return
     */
    public abstract boolean canBindData(T t);

    /**
     * 创建 ViewHolder
     * @param parent
     * @return
     */
    public VH onCreateHolder(ViewGroup parent) {
        View itemView = viewFactory.createView(parent);
        return onCreateHolder(parent, itemView);
    }

    /**
     * 创建 ViewHolder
     * @param parent
     * @param itemView
     * @return
     */
    public abstract VH onCreateHolder(ViewGroup parent, View itemView);

    /**
     * 绑定数据到 View
     * @param holder
     * @param t
     * @param state
     * @param position
     */
    public abstract void onDataBind(VH holder, T t, State state, int position);
}
