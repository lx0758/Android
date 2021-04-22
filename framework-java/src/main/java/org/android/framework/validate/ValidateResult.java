package org.android.framework.validate;

/**
 * date：2018/11/29 18:40
 * author：Liux
 * email：lx0758@qq.com
 * description：
 */

public class ValidateResult {

    public static ValidateResult succeed() {
        return new ValidateResult(true, null);
    }

    public static ValidateResult failure(String message) {
        return new ValidateResult(false, message);
    }

    private boolean succeed;
    private String message;

    public ValidateResult(boolean succeed, String message) {
        this.succeed = succeed;
        this.message = message;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public String getMessage() {
        return message;
    }
}
