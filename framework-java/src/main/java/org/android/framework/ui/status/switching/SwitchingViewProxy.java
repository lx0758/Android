package org.android.framework.ui.status.switching;

import android.view.View;
import android.view.ViewGroup;

import org.android.framework.ui.provider.ISwitchingView;
import org.android.framework.ui.status.OnCancelListener;
import org.android.framework.ui.status.OnRetryListener;

/**
 * 2018/5/7
 * By Liux
 * lx0758@qq.com
 */
public class SwitchingViewProxy implements SwitchingView {

    private View mReplaceView;
    private View mTargetView;
    private ViewGroup mTargetParent;

    private ISwitchingView.IOnCancelView mILoadingView;
    private ISwitchingView.IOnRetryView mIErrorView;
    private ISwitchingView.IOnRetryView mINoDataView;
    private ISwitchingView.IOnRetryView mINoLoginView;
    private ISwitchingView.IOnRetryView mINoNetworkView;

    public SwitchingViewProxy(ISwitchingView iSwitchingView) {
        mILoadingView = iSwitchingView.getLoadingView();
        mIErrorView = iSwitchingView.getErrorView();
        mINoDataView = iSwitchingView.getNoDataView();
        mINoLoginView = iSwitchingView.getNoLoginView();
        mINoNetworkView = iSwitchingView.getNoNetworkView();
    }

    @Override
    public void bind(View view) {
        if (view == null) return;
        mTargetView = view;
        mTargetParent = (ViewGroup) view.getParent();
    }

    @Override
    public void normal() {
        restore();
    }

    @Override
    public void loading(OnCancelListener listener) {
        mILoadingView.setOnCancelListener(listener);
        replace(mILoadingView.getView());
    }

    @Override
    public void error(OnRetryListener listener) {
        mIErrorView.setOnRetryListener(listener);
        replace(mIErrorView.getView());
    }

    @Override
    public void noData(OnRetryListener listener) {
        mINoDataView.setOnRetryListener(listener);
        replace(mINoDataView.getView());
    }

    @Override
    public void noLogin(OnRetryListener listener) {
        mINoLoginView.setOnRetryListener(listener);
        replace(mINoLoginView.getView());
    }

    @Override
    public void noNetwork(OnRetryListener listener) {
        mINoNetworkView.setOnRetryListener(listener);
        replace(mINoNetworkView.getView());
    }

    private void restore() {
        if (mTargetView == null || mTargetParent == null) return;
        if (mReplaceView == null) return;

        int index = mTargetParent.indexOfChild(mReplaceView);
        if (index == -1) return;

        ViewGroup.LayoutParams layoutParams = mReplaceView.getLayoutParams();
        mTargetParent.removeView(mReplaceView);

        mTargetParent.addView(mTargetView, index, layoutParams);
        mReplaceView = null;
    }

    private void replace(View newView) {
        if (newView == null || mTargetView == null || mTargetParent == null) return;
        if (mReplaceView == newView) return;

        View oldView = mReplaceView == null ? mTargetView : mReplaceView;

        int index = mTargetParent.indexOfChild(oldView);
        if (index == -1) return;

        ViewGroup.LayoutParams layoutParams = oldView.getLayoutParams();
        mTargetParent.removeView(oldView);

        mTargetParent.addView(newView, index, layoutParams);
        mReplaceView = newView;
    }
}
