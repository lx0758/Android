package com.liux.android.list.adapter.rule;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则管理器
 */
public class RuleManage<T> {

    private List<Rule<? extends T, ? extends RecyclerView.ViewHolder>> mRules = new ArrayList<>();

    /**
     * 添加规则
     * {@link IRuleAdapter#addRule(Rule)}
     * @param rule
     */
    public void addRule(Rule<? extends T, ? extends RecyclerView.ViewHolder> rule) {
        mRules.add(rule);
    }

    /**
     * 根据数据类型返回规则序号(类型)
     * @param object
     * @return
     */
    public int getRuleType(Object object) {
        for (int index = 0; index < mRules.size(); index++) {
            if (mRules.get(index).canBindObject(object)) {
                return index;
            }
        }
        throw new IllegalArgumentException("No rule of object [" + object + "] was found");
    }

    /**
     * 根据规则序号(类型)查询对应规则
     * @param type
     * @return
     */
    public Rule<? extends T, ? extends RecyclerView.ViewHolder> getTypeRule(int type) {
        return mRules.get(type);
    }

    /**
     * 根据目标数据查找对应规则
     * @param object
     * @return
     */
    public Rule<? extends T, ? extends RecyclerView.ViewHolder> getObjectRule(Object object) {
        for (Rule rule : mRules) {
            if (rule.canBindObject(object)) return rule;
        }
        throw new IllegalArgumentException("No rule of object [" + object + "] was found");
    }
}