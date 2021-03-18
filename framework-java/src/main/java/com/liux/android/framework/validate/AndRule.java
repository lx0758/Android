package com.liux.android.framework.validate;

/**
 * Created by LuoHaifeng on 2018/5/15 0015.
 * Email:496349136@qq.com
 */
public class AndRule extends Rule {
    private Rule[] otherRules;

    public static AndRule instance(Rule... rules) {
        return new AndRule(rules);
    }

    private AndRule(Rule[] otherRules) {
        this.otherRules = otherRules;
    }

    @Override
    public boolean valid() {
        for (Rule rule : otherRules) {
            if (!rule.valid()) {
                return false;
            }
        }
        return true;
    }
}
