package com.liux.android.framework.validate;

/**
 * Created by LuoHaifeng on 2018/5/15 0015.
 * Email:496349136@qq.com
 */
public class OrRule extends Rule {
    private Rule[] otherRules;

    private OrRule(Rule[] otherRules) {
        this.otherRules = otherRules;
    }

    public static OrRule instance(Rule... rules) {
        return new OrRule(rules);
    }

    @Override
    public boolean valid() {
        for (Rule rule : otherRules) {
            boolean result = rule.valid();
            if (result) {
                return true;
            }
        }
        return false;
    }
}
