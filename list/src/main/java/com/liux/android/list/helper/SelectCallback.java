package com.liux.android.list.helper;

/**
 * 选择回调
 * @param <T>
 */
public interface SelectCallback<T> {

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
    void onSelectFailure();

    /**
     * 选择个数达到最大限制数
     */
    void onSelectFull();
}