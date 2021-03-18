package com.liux.android.framework.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.liux.android.abstracts.AbstractsActivity;
import com.liux.android.abstracts.titlebar.TitleBar;
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
 * 2018/11/6
 * By Liux
 * lx0758@qq.com
 */

public abstract class BaseActivity<VB extends ViewBinding> extends AbstractsActivity implements BaseContract.View {

    private StatusView mStatusView;
    private LoadingView mLoadingView;
    private SwitchingView mSwitchingView;

    protected VB mViewBinding;

    @Override
    public TitleBar onInitTitleBar() {
        return getUIProvider().provideTitleBar(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoadingView = new LoadingViewProxy(getILoadingDialog());
        mSwitchingView = new SwitchingViewProxy(getISwitchingView());
        mStatusView = new StatusViewProxy(mLoadingView, mSwitchingView);

        mViewBinding = JetPackUtil.getVB(this);
        setContentView(mViewBinding.getRoot());
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mSwitchingView.bind(getRealContentView());
    }

    @Override
    public UIProvider getUIProvider() {
        if (getApplicationContext() instanceof BaseApplication) {
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
        return getUIProvider().provideILoadingDialog(this);
    }

    protected ISwitchingView getISwitchingView() {
        return getUIProvider().provideISwitchingView(this);
    }

    /**
     * 获取内容布局
     * @return
     */
    private View getRealContentView() {
        ViewGroup viewGroup = findViewById(Window.ID_ANDROID_CONTENT);
        return viewGroup.getChildAt(viewGroup.getChildCount() - 1);
    }
}
