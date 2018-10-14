package com.liux.android.list.adapter.state;

/**
 * 记录选择状态
 * Created by Liux on 2017/9/19.
 */

public class State<T> {

    private T mData;

    public State(T data) {
        this.mData = data;
    }

    public T getData() {
        return mData;
    }



    // 不可选择
    private static final int STATE_SELECT_DISABLED = -1;
    // 未选中
    private static final int STATE_SELECT_UNSELECTED = 0;
    // 已选中
    private static final int STATE_SELECT_SELECTED = 1;

    private int mSelectState = STATE_SELECT_UNSELECTED;

    public boolean isSelectDisabled() {
        return this.mSelectState == STATE_SELECT_DISABLED;
    }
    public void setSelectDisabled() {
        this.mSelectState = STATE_SELECT_DISABLED;
    }

    public boolean isSelectUnselected() {
        return this.mSelectState == STATE_SELECT_UNSELECTED;
    }
    public void setSelectUnselected() {
        this.mSelectState = STATE_SELECT_UNSELECTED;
    }

    public boolean isSelectSelected() {
        return this.mSelectState == STATE_SELECT_SELECTED;
    }
    public void setSelectSelected() {
        this.mSelectState = STATE_SELECT_SELECTED;
    }



    // 不可滑动
    private static final int STATE_SLIDE_DISABLED = -1;
    // 未滑动
    private static final int STATE_SLIDE_UNSLIDE = 0;
    // 已滑动(目前未处理两边滑动的情况)
    private static final int STATE_SLIDE_SLIDED = 1;

    private int mSlideState = STATE_SLIDE_DISABLED;

    public boolean isSlideDisabled() {
        return this.mSelectState == STATE_SLIDE_DISABLED;
    }
    public void setSlideDisabled() {
        this.mSelectState = STATE_SLIDE_DISABLED;
    }

    public boolean isSlideUnslide() {
        return this.mSelectState == STATE_SLIDE_UNSLIDE;
    }
    public void setSlideUnslide() {
        this.mSelectState = STATE_SELECT_UNSELECTED;
    }

    public boolean isSlideSlided() {
        return this.mSelectState == STATE_SLIDE_SLIDED;
    }
    public void setSlideSlided() {
        this.mSelectState = STATE_SLIDE_SLIDED;
    }



    private Object mTag = null;

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }



    @Override
    public String toString() {
        return "[" + mSelectState + "," + mSlideState + "," + mTag + ']';
    }
}
