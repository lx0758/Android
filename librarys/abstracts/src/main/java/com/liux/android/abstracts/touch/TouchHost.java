package com.liux.android.abstracts.touch;

import android.view.View;

/**
 * Created by Liux on 2017/12/4.
 */

public interface TouchHost {

    /**
     * 获取是否处理了全局触控事件
     * @return
     */
    boolean isHandlerTouch();

    /**
     * 设置是否处理全局触控事件
     * @param handlerTouch
     */
    void setHandlerTouch(boolean handlerTouch);

    /**
     * 是否忽略了某个View
     * @param view
     * @return
     */
    boolean hasIgnoreView(View view);

    /**
     * 添加忽略某个View
     * @param view
     */
    void addIgnoreView(View view);

    /**
     * 移除忽略某个View
     * @param view
     */
    void removeIgnoreView(View view);

    /**
     * 添加触摸隐藏软键盘回调
     * @param touchCallback
     */
    void addTouchCallback(TouchCallback touchCallback);

    /**
     * 移除触摸隐藏软键盘回调
     * @param touchCallback
     */
    void removeTouchCallback(TouchCallback touchCallback);
}
