package com.liux.android.framework.validate;

public class CallRule extends Rule {
    private Callback callback;

    public static CallRule instance(Callback callback) {
        return new CallRule(callback);
    }

    private CallRule(Callback callback) {
        this.callback = callback;
    }

    @Override
    public boolean valid() {
        return callback.onValid();
    }

    public interface Callback {

        boolean onValid();
    }
}
