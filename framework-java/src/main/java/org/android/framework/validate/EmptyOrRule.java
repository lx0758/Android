package org.android.framework.validate;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by LuoHaifeng on 2018/5/15 0015.
 * Email:496349136@qq.com
 */
public class EmptyOrRule extends Rule {
    private String object;
    private Rule rule;

    private EmptyOrRule(String object, Rule rule) {
        this.object = object;
        this.rule = rule;
    }

    public static EmptyOrRule instance(TextView textView, boolean trim, Rule rule) {
        String data = textView.getText().toString();
        if (trim) {
            data = data.trim();
        }
        return new EmptyOrRule(data, rule);
    }

    public static EmptyOrRule instance(String data, Rule rule) {
        return new EmptyOrRule(data, rule);
    }

    @Override
    public boolean valid() {
        if (TextUtils.isEmpty(object)) {
            return true;
        } else {
            return rule.valid();
        }
    }
}
