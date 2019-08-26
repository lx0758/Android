package com.liux.android.list.adapter.rule;

import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.liux.android.list.adapter.state.State;

/**
 * Bean 和 View 绑定显示规则
 * @param <T> 要处理的类型
 */
public abstract class Rule<T, VH extends RecyclerView.ViewHolder> {

    public int layout;

    /**
     * 规则绑定目标 Layout
     * @param layout
     */
    public Rule(@LayoutRes int layout) {
        this.layout = layout;
    }

    /**
     * 判断数据是否匹配规则
     * @param object
     * @return
     */
    public boolean doBindObject(Object object) {
        try {
            T t = (T) object;
            return doBindData(t);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * 数据类型相同,是否匹配规则
     * @param t
     * @return
     */
    public abstract boolean doBindData(T t);

    /**
     * 创建ViewHolder
     * @param parent
     * @param layout
     * @return
     */
    public abstract VH createHolder(ViewGroup parent, int layout);

    /**
     * 绑定数据到View
     * @param holder
     * @param t
     * @param state
     * @param position
     */
    public abstract void onDataBind(VH holder, T t, State state, int position);
}
