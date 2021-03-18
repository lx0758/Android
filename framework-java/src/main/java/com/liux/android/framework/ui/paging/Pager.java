package com.liux.android.framework.ui.paging;

import com.liux.android.framework.rx.IResp;

import java.util.List;

/**
 * 分页器
 */
public class Pager<T> {

    // 每一页请求数量
    private int pageSize;
    // 是否从第0页开始
    private boolean isZeroStart;
    // 数据源
    private List<T> data;

    private PagingView pagingView;
    private PagingRequest.Handler<IResp<List<T>>> pagingRequestHandler;
    private PagingResponseHandler<T, IResp<List<T>>> pagingResponseHandler;

    private PagingRequest.Callback<IResp<List<T>>> refreshCallback = new PagingRequest.Callback<IResp<List<T>>>() {
        @Override
        public void onSuccess(IResp<List<T>> resp) {
            List<T> datas = pagingResponseHandler.onResponse(resp);
            boolean noMore = false;
            int size = 0;

            data.clear();
            if (datas != null && !datas.isEmpty()) {
                data.addAll(datas);
                size = datas.size();
                if (size < pageSize || !pagingResponseHandler.hasMore()) {
                    noMore = true;
                }
            } else {
                noMore = true;
            }

            pagingView.finishRefresh(size);
            pagingView.setNoMore(noMore);
        }

        @Override
        public void onError(Throwable e) {
            data.clear();
            pagingView.finishRefresh(0);
        }
    };

    public Pager(int pageSize, boolean isZeroStart, List<T> data, PagingView pagingView, PagingRequest.Handler<IResp<List<T>>> pagingRequestHandler, PagingResponseHandler<T, IResp<List<T>>> pagingResponseHandler) {
        this.pageSize = pageSize;
        this.isZeroStart = isZeroStart;
        this.data = data;
        this.pagingView = pagingView;
        this.pagingRequestHandler = pagingRequestHandler;
        this.pagingResponseHandler = pagingResponseHandler;

        pagingView.bindView(this);
    }

    private PagingRequest.Callback<IResp<List<T>>> loadMoreCallback = new PagingRequest.Callback<IResp<List<T>>>() {
        @Override
        public void onSuccess(IResp<List<T>> resp) {
            List<T> datas = pagingResponseHandler.onResponse(resp);
            boolean noMore = false;
            int size = 0;

            if (datas != null && !datas.isEmpty()) {
                data.addAll(datas);
                size = datas.size();
                if (size < pageSize || !pagingResponseHandler.hasMore()) {
                    noMore = true;
                }
            } else {
                noMore = true;
            }

            pagingView.finishLoadMore(size);
            pagingView.setNoMore(noMore);
        }

        @Override
        public void onError(Throwable e) {
            pagingView.finishLoadMore(0);
        }
    };

    public void onRefresh() {
        int nextPage = nextPage(0, pageSize, isZeroStart);
        pagingRequestHandler.onRequest(new PagingRequest<>(
                true,
                true,
                data.isEmpty(),
                0,
                pageSize,
                nextPage,
                pageSize,
                refreshCallback
        ));
    }

    public void onLoadMore() {
        int nextPage = nextPage(data.size(), pageSize, isZeroStart);
        pagingRequestHandler.onRequest(new PagingRequest<>(
                false,
                false,
                data.isEmpty(),
                data.size(),
                data.size() + pageSize,
                nextPage,
                pageSize,
                loadMoreCallback
        ));
    }

    /**
     * 计算下一页的页码
     * @param localSize 当前数据源尺寸
     * @param pageSize 请求每页条数
     * @param isZeroStart 是否从第0页开始
     * @return 下一页的页码
     */
    public static int nextPage(int localSize, int pageSize, boolean isZeroStart) {
        if (localSize == 0) return isZeroStart ? 0 : 1;
        return localSize / pageSize + ((localSize % pageSize) == 0 ? 0 : 1) + (isZeroStart ? 0 : 1);
    }
}
