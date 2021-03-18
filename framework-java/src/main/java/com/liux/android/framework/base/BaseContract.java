package com.liux.android.framework.base;

import androidx.lifecycle.LifecycleOwner;

import com.liux.android.framework.ui.provider.UIProvider;
import com.liux.android.framework.ui.status.StatusView;
import com.liux.android.framework.ui.status.loading.LoadingView;
import com.liux.android.framework.ui.status.switching.SwitchingView;

/**
 * 2016/12/1
 * By Liux
 * lx0758@qq.com
 */

public class BaseContract {

    public interface View extends LifecycleOwner {

        UIProvider getUIProvider();

        StatusView getStatusView();

        LoadingView getLoadingView();

        SwitchingView getSwitchingView();
    }

    public interface Presenter<V extends View> {

        void bindView(V view);
    }
}
