package org.android.framework.validate;

import androidx.annotation.NonNull;

/**
 * Created by LuoHaifeng on 2018/4/24 0024.
 * Email:496349136@qq.com
 */
public class InRule extends Rule {
    private Object obj;
    private Object[] range;
    private boolean isAssertIn = true;

    public InRule(Object obj, @NonNull Object[] range, boolean isAssertIn) {
        this.obj = obj;
        this.range = range;
        this.isAssertIn = isAssertIn;
    }

    public static InRule isIn(Object obj, @NonNull Object[] range) {
        return new InRule(obj, range, true);
    }

    public static InRule notIn(Object obj, @NonNull Object[] range) {
        return new InRule(obj, range, false);
    }

    @Override
    public boolean valid() {
        boolean isIn = false;
        if (obj == null) {
            for (Object rangeValue : range) {
                if (rangeValue == null) {
                    isIn = true;
                    break;
                }
            }
        } else {
            for (Object rangeValue : range) {
                if (obj.equals(rangeValue)) {
                    isIn = true;
                    break;
                }
            }
        }

        return isAssertIn == isIn;
    }
}
