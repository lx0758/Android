package com.liux.android.list.adapter.rule;

import android.support.v7.widget.RecyclerView;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public class RuleProxy<T> {

    private IRule mIRule;

    private RuleManage mRuleManage = new RuleManage();

    public RuleProxy(IRule iRule) {
        mIRule = iRule;
    }

    public void addRule(Rule rule) {
        mRuleManage.addRule(rule);
    }

    public int getRuleType(T t) {
        return mRuleManage.getRuleType(t);
    }

    public Rule getTypeRule(int viewType) {
        return mRuleManage.getTypeRule(viewType);
    }

    public Rule getObjectRule(T t) {
        return mRuleManage.getObjectRule(t);
    }
}
