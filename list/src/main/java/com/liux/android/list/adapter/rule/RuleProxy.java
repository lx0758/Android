package com.liux.android.list.adapter.rule;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public class RuleProxy<T> {

    private IRule mIRule;

    private RuleManage<T> mRuleManage = new RuleManage<>();

    public RuleProxy(IRule iRule) {
        mIRule = iRule;
    }

    public void addRule(Rule<? extends T, ? extends RecyclerView.ViewHolder> rule) {
        mRuleManage.addRule(rule);
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
