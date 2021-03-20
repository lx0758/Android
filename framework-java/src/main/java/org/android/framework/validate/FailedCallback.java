package org.android.framework.validate;

public interface FailedCallback {

    /**
     * 验证失败
     * @param rule
     * @param message
     */
    void onFailed(Rule rule, String message);
}
