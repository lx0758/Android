package com.liux.android.list.listener;

/**
 * 选择事件监听器
 * @param <T>
 */
public interface OnSelectListener<T> {

    /**
     * 选择状态变化
     * @param t
     * @param position
     * @param select
     * @return
     */
    boolean onSelectPre(T t, int position, boolean select);

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