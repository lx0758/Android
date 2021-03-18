package com.liux.android.framework.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.liux.android.abstracts.AbstractsFragment;
import com.liux.android.framework.ui.provider.ILoadingDialog;
import com.liux.android.framework.ui.provider.ISwitchingView;
import com.liux.android.framework.ui.provider.UIProvider;
import com.liux.android.framework.ui.status.StatusView;
import com.liux.android.framework.ui.status.StatusViewProxy;
import com.liux.android.framework.ui.status.loading.LoadingView;
import com.liux.android.framework.ui.status.loading.LoadingViewProxy;
import com.liux.android.framework.ui.status.switching.SwitchingView;
import com.liux.android.framework.ui.status.switching.SwitchingViewProxy;
import com.liux.android.framework.util.JetPackUtil;

/**
 * 2017/11/6
 * By Liux
 * lx0758@qq.com
 */

public abstract class BaseFragment<VB extends ViewBinding> extends AbstractsFragment implements BaseContract.View {

    protected VB mViewBinding;

    private StatusView mStatusView;
    private LoadingView mLoadingView;
    private SwitchingView mSwitchingView;
    private FrameLayout mWrapperFrameLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoadingView = new LoadingViewProxy(getILoadingDialog());
        mSwitchingView = new SwitchingViewProxy(getISwitchingView());
        mStatusView = new StatusViewProxy(mLoadingView, mSwitchingView);
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = JetPackUtil.getVB(this, inflater, container);

        mWrapperFrameLayout = new FrameLayout(inflater.getContext());
        mWrapperFrameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mWrapperFrameLayout.addView(mViewBinding.getRoot());
        mSwitchingView.bind(mWrapperFrameLayout);

        return mWrapperFrameLayout;
    }

    @Override
    public UIProvider getUIProvider() {
        if (getContext().getApplicationContext() instanceof BaseApplication) {
            return BaseApplication.getUIProvide();
        }
        throw new NullPointerException("No object provides UIProvider");
    }

    @Override
    public StatusView getStatusView() {
        return mStatusView;
    }

    @Override
    public LoadingView getLoadingView() {
        return mLoadingView;
    }

    @Override
    public SwitchingView getSwitchingView() {
        return mSwitchingView;
    }

    protected ILoadingDialog getILoadingDialog() {
        return getUIProvider().provideILoadingDialog(getContext());
    }

    protected ISwitchingView getISwitchingView() {
        return getUIProvider().provideISwitchingView(getContext());
    }
}
