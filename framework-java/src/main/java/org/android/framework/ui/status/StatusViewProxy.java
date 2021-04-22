package org.android.framework.ui.status;

import org.android.framework.ui.status.loading.LoadingView;
import org.android.framework.ui.status.switching.SwitchingView;

/**
 * 状态切换控制类
 * 1. 加载时会根据状态切换视图
 * 2. 支持加载中/错误/无数据/未登录/无网络状态
 * 3. 成功加载之后不会再切换到"加载中"视图,直到出现错误
 * 2018/5/4
 * By Liux
 * lx0758@qq.com
 */
public class StatusViewProxy implements StatusView {

    /**
     * 当前状态
     */
    private Status mStatus = Status.INITIAL;

    private LoadingView mLoadingView;
    private SwitchingView mSwitchingView;

    public StatusViewProxy(LoadingView loadingView, SwitchingView switchingView) {
        mLoadingView = loadingView;
        mSwitchingView = switchingView;
    }

    @Override
    public Status status() {
        return mStatus;
    }

    @Override
    public void normal(Object tag) {
        hideLoadingView(tag);

        mStatus = Status.NORMAL;
        mSwitchingView.normal();
    }

    @Override
    public void loading(Object tag, OnCancelListener listener, boolean showLoadingView) {
        mStatus = Status.LOADING;

        if (showLoadingView) {
            mLoadingView.show(tag, false, listener);
        } else {
            mSwitchingView.loading(listener);
        }
    }

    @Override
    public void error(Object tag, OnRetryListener listener) {
        hideLoadingView(tag);

        mStatus = Status.ERROR;
        mSwitchingView.error(listener);
    }

    @Override
    public void noData(Object tag, OnRetryListener listener) {
        hideLoadingView(tag);

        mStatus = Status.NO_DATA;
        mSwitchingView.noData(listener);
    }

    @Override
    public void noLogin(Object tag, OnRetryListener listener) {
        hideLoadingView(tag);

        mStatus = Status.NO_LOGIN;
        mSwitchingView.noLogin(listener);
    }

    @Override
    public void noNetwork(Object tag, OnRetryListener listener) {
        hideLoadingView(tag);

        mStatus = Status.NO_NETWORK;
        mSwitchingView.noNetwork(listener);
    }

    /**
     * 隐藏显示状态的 LoadingView
     * @param tag
     */
    private void hideLoadingView(Object tag) {
        mLoadingView.hide(tag);
    }
}
