package org.android.framework.ui.selector;

import androidx.fragment.app.FragmentManager;

import java.util.List;

public class Selector {

    private SelectorRequest.Handler handler;
    private SelectorCallback selectorCallback;

    private SelectorDialog selectorDialog;

    public Selector(SelectorRequest.Handler handler, SelectorCallback selectorCallback) {
        this.handler = handler;
        this.selectorCallback = selectorCallback;

        this.selectorDialog = new SelectorDialog(this, handler, selectorCallback);
    }

    public Selector setTitle(String title) {
        selectorDialog.setTitle(title);
        return this;
    }

    public Selector setMaxLevel(int maxLevel) {
        selectorDialog.setMaxLevel(maxLevel);
        return this;
    }

    public void show(FragmentManager fragmentManager, String tag) {
        handler.request(new SelectorRequest(selectorDialog), 1, null, new SelectorRequest.Callback() {
            @Override
            public void onShow(List<? extends SelectorBean> selectorBeanList) {
                if (selectorBeanList == null || selectorBeanList.isEmpty()) {
                    selectorCallback.onEmptyData(Selector.this, 1, null);
                    return;
                }
                selectorDialog.show(selectorBeanList, fragmentManager, tag);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void dismiss() {
        if (selectorDialog.isAdded()) {
            selectorDialog.dismiss();
        }
    }
}
