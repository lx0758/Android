package com.liux.android.list.helper;

/**
 * 选择回调
 * @param <T>
 */
public interface SelectCallback<T> {

    int TYPE_NOT_SUPPORT = 1;
    int TYPE_CALLBACK_PROHIBIT = 2;
    int TYPE_COUNT_FULL = 3;

    /**
     * 选择前回调
     * @return
     */
    boolean onSelectBefore(T t);

    /**
     * 选择后回调
     * @return
     */
    void onSelect(T t, boolean selected);

    /**
     * 选择个数大于最大限制数
     * 或者数据不支持选择
     */
    void onSelectFailure(int type);

    /**
     * 选择个数达到最大限制数
     */
    void onSelectFull();
}