package org.android.framework.validate;

public class ValidateRequest {

    private Rule[] rules;

    ValidateRequest(Rule[] rules) {
        this.rules = rules;
    }

    public ValidateResult validate() {
        if (rules != null) {
            for (Rule rule : rules) {
                if (!rule.valid()) return ValidateResult.failure(rule.getMessage());
            }
        }
        return ValidateResult.succeed();
    }

    public boolean validate(SucceedCallback succeedCallback, FailedCallback failedCallback) {
        if (rules == null) return true;
        for (Rule rule : rules) {
            if (!rule.valid()) {
                if (failedCallback != null) failedCallback.onFailed(rule, rule.getMessage());
                return false;
            };
        }
        if (succeedCallback != null) succeedCallback.onSucceed();
        return true;
    }

    public boolean validate(ResultCallback resultCallback) {
        return validate(resultCallback, resultCallback);
    }
}
