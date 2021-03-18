package com.liux.android.framework.ui.paging;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 分页请求
 * @param <R>
 */
public class PagingRequest<R> {
    /**
     * 是否是第一次有效请求
     */
    private boolean isFirst;
    /**
     * 是否是刷新请求
     */
    private boolean isRefresh;
    /**
     * 本地数据源是否为空
     */
    private boolean isEmpty;
    /**
     * 请求起始位置
     */
    private int startPos;
    /**
     * 请求终止位置
     */
    private int endPos;
    /**
     * 请求页码
     */
    private int page;
    /**
     * 请求页长度
     */
    private int pageSize;
    /**
     * 请求数据回调
     */
    private Callback<R> callback;

    PagingRequest(boolean isFirst, boolean isRefresh, boolean isEmpty, int startPos, int endPos, int page, int pageSize, Callback<R> callback) {
        this.isFirst = isFirst;
        this.isRefresh = isRefresh;
        this.isEmpty = isEmpty;
        this.startPos = startPos;
        this.endPos = endPos;
        this.page = page;
        this.pageSize = pageSize;
        this.callback = callback;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Callback<R> getCallback() {
        return callback;
    }

    /**
     * 分页请求处理器
     */
    public interface Handler<R> {

        /**
         * 处理分页请求
         * @param pagingRequest
         */
        void onRequest(PagingRequest<R> pagingRequest);
    }

    /**
     * 分页请求回调
     */
    public static abstract class Callback<R> implements SingleObserver<R> {

        @Override
        public void onSubscribe(Disposable d) {

        }
    }
}
