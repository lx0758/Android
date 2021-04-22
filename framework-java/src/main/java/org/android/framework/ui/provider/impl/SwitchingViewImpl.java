package org.android.framework.ui.provider.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import org.android.framework.R;
import org.android.framework.ui.provider.ISwitchingView;
import org.android.framework.ui.status.OnCancelListener;
import org.android.framework.ui.status.OnRetryListener;

/**
 * 2018/5/9
 * By Liux
 * lx0758@qq.com
 */
public class SwitchingViewImpl implements ISwitchingView {

    private Context mContext;
    private ISwitchingView.IOnCancelView mILoadingView;
    private ISwitchingView.IOnRetryView mIErrorView;
    private ISwitchingView.IOnRetryView mINoDataView;
    private ISwitchingView.IOnRetryView mINoLoginView;
    private ISwitchingView.IOnRetryView mINoNetworkView;

    public SwitchingViewImpl(Context context) {
        mContext = context;
        mILoadingView = new IViewImpl(mContext, R.layout.view_switching_loading);
        mIErrorView = new IViewImpl(mContext, R.layout.view_switching_error, R.id.btn_retry);
        mINoDataView = new IViewImpl(mContext, R.layout.view_switching_no_data, R.id.btn_retry);
        mINoLoginView = new IViewImpl(mContext, R.layout.view_switching_no_login, R.id.btn_retry);
        mINoNetworkView = new IViewImpl(mContext, R.layout.view_switching_no_network, R.id.btn_retry);
    }

    @Override
    public IOnCancelView getLoadingView() {
        return mILoadingView;
    }

    @Override
    public IOnRetryView getErrorView() {
        return mIErrorView;
    }

    @Override
    public IOnRetryView getNoDataView() {
        return mINoDataView;
    }

    @Override
    public IOnRetryView getNoLoginView() {
        return mINoLoginView;
    }

    @Override
    public IOnRetryView getNoNetworkView() {
        return mINoNetworkView;
    }

    @Override
    public ISwitchingView setLoadingView(IOnCancelView iOnCancelView) {
        mILoadingView = iOnCancelView;
        return this;
    }

    @Override
    public ISwitchingView setErrorView(IOnRetryView iOnRetryView) {
        mIErrorView = iOnRetryView;
        return this;
    }

    @Override
    public ISwitchingView setNoDataView(IOnRetryView iOnRetryView) {
        mINoDataView = iOnRetryView;
        return this;
    }

    @Override
    public ISwitchingView setNoLoginView(IOnRetryView iOnRetryView) {
        mINoLoginView = iOnRetryView;
        return this;
    }

    @Override
    public ISwitchingView setNoNetworkView(IOnRetryView iOnRetryView) {
        mINoNetworkView = iOnRetryView;
        return this;
    }

    public static class IViewImpl implements IOnCancelView, IOnRetryView, View.OnClickListener {
        private OnCancelListener mOnCancelListener;
        private OnRetryListener mOnRetryListener;
        private View mView;

        public IViewImpl(Context context, int layout, int... ids) {
            mView = LayoutInflater.from(context).inflate(layout, null);
            if (ids != null) {
                for (int id : ids) {
                    mView.findViewById(id).setOnClickListener(this);
                }
            }
            onInitView(mView);
        }

        @Override
        public View getView() {
            return mView;
        }

        @Override
        public void setOnCancelListener(OnCancelListener listener) {
            mOnCancelListener = listener;
        }

        @Override
        public void setOnRetryListener(OnRetryListener listener) {
            mOnRetryListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (mOnCancelListener != null) mOnCancelListener.onCancel();
            if (mOnRetryListener != null) mOnRetryListener.onRetry();
        }

        protected void onInitView(View view) {

        }
    }
}
