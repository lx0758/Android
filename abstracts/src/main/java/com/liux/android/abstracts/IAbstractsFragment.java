package com.liux.android.abstracts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.liux.android.abstracts.touch.TouchCallback;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

public interface IAbstractsFragment extends TouchCallback {

    Fragment getTarget();

    // ===============================================================

    /**
     * 懒加载模式,保证创建完成后第一次显示时调用一次 <br>
     * {@link Fragment#onViewCreated(View, Bundle)}
     * {@link Fragment#setUserVisibleHint(boolean)}
     */
    void onLazyLoad();

    /**
     * 每次重新在前台展示时调用,与 {@link #onLazyLoad} 互斥
     * {@link Fragment#onStart()} 后调用
     * {@link Fragment#onHiddenChanged(boolean)} 后调用
     */
    void onVisibleChanged();
}
