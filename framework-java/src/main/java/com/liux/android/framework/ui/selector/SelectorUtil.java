package com.liux.android.framework.ui.selector;

public class SelectorUtil {

    public static Selector createSingle(SelectorRequest.Handler handler, SelectorSingleCallback selectorSingleCallback) {
        return create(handler, selectorSingleCallback).setMaxLevel(1);
    }

    public static Selector create(SelectorRequest.Handler handler, SelectorCallback selectorCallback) {
        return new Selector(handler, selectorCallback);
    }
}
