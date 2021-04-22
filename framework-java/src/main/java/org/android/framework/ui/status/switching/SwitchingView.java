package org.android.framework.ui.status.switching;

import android.view.View;

import org.android.framework.ui.status.OnCancelListener;
import org.android.framework.ui.status.OnRetryListener;

/**
 * 2018/5/7
 * By Liux
 * lx0758@qq.com
 */
public interface SwitchingView {

    void bind(View view);

    void normal();

    void loading(OnCancelListener listener);

    void error(OnRetryListener listener);

    void noData(OnRetryListener listener);

    void noLogin(OnRetryListener listener);

    void noNetwork(OnRetryListener listener);
}
