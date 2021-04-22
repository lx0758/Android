package org.android.framework.ui.selector;

import java.util.List;

/**
 * 选择事件回调
 */
public interface SelectorCallback {

    void onSelected(List<SelectorBean> selectedBeans);

    void onEmptyData(Selector selector, int level, SelectorBean lastLevelSelectedBean);
}
