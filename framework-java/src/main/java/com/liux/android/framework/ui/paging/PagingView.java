package com.liux.android.framework.ui.paging;

/**
 * 分页列表视图
 */
public interface PagingView {

    /**
     * 绑定分页器
     * @param pager
     */
    void bindView(Pager pager);

    /**
     * 刷新适配器及恢复UI完成刷新动作
     * @param size
     */
    void finishRefresh(int size);

    /**
     * 刷新适配器及恢复UI完成加载动作
     * @param size
     */
    void finishLoadMore(int size);

    /**
     * 设置是否还有更多数据
     * @param noMore
     */
    void setNoMore(boolean noMore);
}
