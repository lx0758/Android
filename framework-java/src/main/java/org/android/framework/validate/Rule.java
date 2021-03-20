package org.android.framework.validate;

import androidx.annotation.StringRes;

/**
 * Created by LuoHaifeng on 2018/4/24 0024.
 * Email:496349136@qq.com
 */
public abstract class Rule {

    private String message;

    public abstract boolean valid();

    public Rule setMessage(String message) {
        this.message = message;
        return this;
    }

    public Rule setMessage(@StringRes int messageResId) {
        this.message = Validator.getContext().getString(messageResId);
        return this;
    }

    public String getMessage() {
        return message;
    }
}
