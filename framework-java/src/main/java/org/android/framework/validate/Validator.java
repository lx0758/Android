package org.android.framework.validate;

import android.content.Context;

/**
 * Created by LuoHaifeng on 2018/4/24 0024.
 * Email:496349136@qq.com
 */
public class Validator {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        if (context == null) throw new NullPointerException("Context cannot be empty");
        Validator.context = context.getApplicationContext();
    }

    public static ValidateRequest create(Rule... rules) {
        return new ValidateRequest(rules);
    }
}
