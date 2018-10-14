package com.liux.android.list.adapter.rule;

import android.support.v7.widget.RecyclerView;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public interface IRule<T, R extends RecyclerView.Adapter> {

    /**
     * 添加数据和视图关联规则
     * @param rule 适配规则
     * @return 当前实例
     */
    R addRule(Rule<? extends T, ? extends RecyclerView.ViewHolder> rule);
}
