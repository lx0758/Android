package com.liux.android.list.adapter.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则管理器
 */
public class RuleManage {

    private List<Rule> mRules = new ArrayList<>();

    /**
     * 添加规则
     * {@link IRule#addRule(Rule)}
     * @param rule
     */
    public void addRule(Rule rule) {
        mRules.add(rule);
    }

    /**
     * 根据数据类型返回规则序号(类型)
     * @param object
     * @return
     */
    public int getRuleType(Object object) {
        for (int index = 0; index < mRules.size(); index++) {
            if (mRules.get(index).doBindObject(object)) {
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
    public Rule getTypeRule(int type) {
        return mRules.get(type);
    }

    /**
     * 根据目标数据查找对应规则
     * @param object
     * @return
     */
    public Rule getObjectRule(Object object) {
        for (Rule rule : mRules) {
            if (rule.doBindObject(object)) return rule;
        }
        throw new IllegalArgumentException("No rule of object [" + object + "] was found");
    }
}