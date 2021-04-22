package org.android.framework.ui.status;

/**
 * 2018/5/3
 * By Liux
 * lx0758@qq.com
 */
public interface StatusView {

    /**
     * 获取当前状态
     * @return
     */
    Status status();

    /**
     * 切换到正常状态
     * @param tag
     */
    void normal(Object tag);

    /**
     * 切换到加载中状态
     * @param tag
     * @param listener
     * @param showLoadingView 是否只显示"加载中"弹窗而不显示"加载中"视图
     */
    void loading(Object tag, OnCancelListener listener, boolean showLoadingView);

    /**
     * 切换到错误状态
     * @param tag
     * @param listener
     */
    void error(Object tag, OnRetryListener listener);

    /**
     * 切换到无数据状态
     * @param tag
     * @param listener
     */
    void noData(Object tag, OnRetryListener listener);

    /**
     * 切换到未登录状态
     * @param tag
     * @param listener
     */
    void noLogin(Object tag, OnRetryListener listener);

    /**
     * 切换到无网络状态
     * @param tag
     * @param listener
     */
    void noNetwork(Object tag, OnRetryListener listener);
}
