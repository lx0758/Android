package com.liux.android.list.adapter.state;

import java.util.Locale;

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

    private static final int BIT_SUPPORT_SELECT = 0;
    private static final int BIT_SELECTED = 1;
    private static final int BIT_SUPPORT_SLIDE = 2;
    private static final int BIT_SLIDE_OPEN = 3;
    private int mState = 0;
    private boolean get(int bit) {
        return ((mState >> bit) & 0b1) == 0b1;
    }
    private void set(int bit, boolean b) {
        if (b == get(bit)) return;
        if (b) {
            mState |= (1 << bit);
        } else {
            mState ^= (1 << bit);
        }
    }
    public boolean isSupportSelect() {
        return get(BIT_SUPPORT_SELECT);
    }
    public void setSupportSelect(boolean support) {
        set(BIT_SUPPORT_SELECT, support);
    }
    public boolean isSelected() {
        return get(BIT_SELECTED);
    }
    public void setSelected(boolean selected) {
        set(BIT_SELECTED, selected);
    }
    public boolean isSupportSlide() {
        return get(BIT_SUPPORT_SLIDE);
    }
    public void setSupportSlide(boolean support) {
        set(BIT_SUPPORT_SLIDE, support);
    }
    public boolean isSlideOpen() {
        return get(BIT_SLIDE_OPEN);
    }
    public void setSlideOpen(boolean open) {
        set(BIT_SLIDE_OPEN, open);
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
        return String.format(Locale.CHINA, "[state:0b%08d, tag:%s]", Integer.parseInt(Integer.toBinaryString(mState)), mTag);
    }
}
