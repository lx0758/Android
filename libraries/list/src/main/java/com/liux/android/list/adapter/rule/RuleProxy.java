package com.liux.android.list.adapter.rule;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public class RuleProxy<T, R extends RecyclerView.Adapter> implements IRuleAdapter<T, R> {

    private R mAdapter;

    private RuleManage<T> mRuleManage = new RuleManage<>();

    public RuleProxy(R adapter) {
        mAdapter = adapter;
    }

    @Override
    public R addRule(Rule<? extends T, ? extends RecyclerView.ViewHolder> rule) {
        mRuleManage.addRule(rule);
        return mAdapter;
    }

    public int getRuleType(T t) {
        return mRuleManage.getRuleType(t);
    }

    public Rule<? extends T, ? extends RecyclerView.ViewHolder> getTypeRule(int viewType) {
        return mRuleManage.getTypeRule(viewType);
    }

    public Rule<? extends T, ? extends RecyclerView.ViewHolder> getObjectRule(T t) {
        return mRuleManage.getObjectRule(t);
    }
}
