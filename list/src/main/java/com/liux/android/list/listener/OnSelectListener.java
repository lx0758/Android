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
     * @param isSelect
     * @return
     */
    boolean onSelectChange(T t, int position, boolean isSelect);

    /**
     * 选择个数大于最大限制数
     */
    void onSelectFailure();

    /**
     * 选择个数达到最大限制数
     */
    void onSelectComplete();
}