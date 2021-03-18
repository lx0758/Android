package com.liux.android.framework.base;

import android.content.Context;

import androidx.viewbinding.ViewBinding;

/**
 * 2017/11/6
 * By Liux
 * lx0758@qq.com
 */

public abstract class BaseMvpFragment<VB extends ViewBinding, P extends BaseContract.Presenter> extends BaseFragment<VB>
        implements BaseContract.View {

    protected P mPresenter;

    protected abstract P createPresenter();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.bindView(this);
        }
    }
}
