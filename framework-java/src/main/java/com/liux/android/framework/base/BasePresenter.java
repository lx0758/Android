package com.liux.android.framework.base;

/**
 * 2017/8/17
 * By Liux
 * lx0758@qq.com
 */

public class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {

    protected V mView;

    @Override
    public void bindView(V view) {
        mView = view;
    }
}
