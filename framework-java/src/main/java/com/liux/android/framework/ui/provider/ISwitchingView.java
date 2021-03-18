package com.liux.android.framework.ui.provider;

import android.view.View;

import com.liux.android.framework.ui.status.OnCancelListener;
import com.liux.android.framework.ui.status.OnRetryListener;

/**
 * 2018/5/9
 * By Liux
 * lx0758@qq.com
 */
public interface ISwitchingView {

    IOnCancelView getLoadingView();

    IOnRetryView getErrorView();

    IOnRetryView getNoDataView();

    IOnRetryView getNoLoginView();

    IOnRetryView getNoNetworkView();

    ISwitchingView setLoadingView(IOnCancelView iOnCancelView);

    ISwitchingView setErrorView(IOnRetryView iOnRetryView);

    ISwitchingView setNoDataView(IOnRetryView iOnRetryView);

    ISwitchingView setNoLoginView(IOnRetryView iOnRetryView);

    ISwitchingView setNoNetworkView(IOnRetryView iOnRetryView);

    interface IOnCancelView extends IView {

        void setOnCancelListener(OnCancelListener listener);
    }

    interface IOnRetryView extends IView {

        void setOnRetryListener(OnRetryListener listener);
    }

    interface IView {

        View getView();
    }
}
