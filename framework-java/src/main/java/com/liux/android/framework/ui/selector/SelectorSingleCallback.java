package com.liux.android.framework.ui.selector;

import android.text.TextUtils;

import com.liux.android.framework.ui.provider.UIProvider;

import java.util.List;

public abstract class SelectorSingleCallback implements SelectorCallback {

    private UIProvider uiProvider;
    private String emptyDataString;

    public SelectorSingleCallback() {
        this(null, null);
    }

    public SelectorSingleCallback(UIProvider uiProvider) {
        this(uiProvider, null);
    }

    public SelectorSingleCallback(UIProvider uiProvider, String emptyDataString) {
        this.uiProvider = uiProvider;
        if (TextUtils.isEmpty(emptyDataString)) emptyDataString = "没有供选择的数据";
        this.emptyDataString = emptyDataString;
    }

    @Override
    public void onEmptyData(Selector selector, int level, SelectorBean lastLevelSelectedBean) {
        if (uiProvider != null) uiProvider.provideIToast().showWarn(emptyDataString);
        selector.dismiss();
    }

    @Override
    public void onSelected(List<SelectorBean> selectedBeans) {
        onSelected(selectedBeans.get(0));
    }

    public abstract void onSelected(SelectorBean selectorBean);
}
