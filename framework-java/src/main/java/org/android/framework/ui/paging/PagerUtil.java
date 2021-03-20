package org.android.framework.ui.paging;

import androidx.recyclerview.widget.RecyclerView;

import org.android.framework.rx.IResp;
import com.liux.android.list.adapter.MultipleAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public class PagerUtil {

    public static <T> Pager<T> create(SmartRefreshLayout smartRefreshLayout, MultipleAdapter<T> multipleAdapter, PagingRequest.Handler<IResp<List<T>>> handler) {
        return create(10, false, multipleAdapter.getData(), smartRefreshLayout, multipleAdapter, handler);
    }

    public static <T> Pager<T> create(List<T> list, SmartRefreshLayout smartRefreshLayout, RecyclerView.Adapter adapter, PagingRequest.Handler<IResp<List<T>>> handler) {
        return create(10, false, list, smartRefreshLayout, adapter, handler);
    }

    public static <T> Pager<T> create(int pageSize, boolean isZeroStart, List<T> list, SmartRefreshLayout smartRefreshLayout, RecyclerView.Adapter adapter, PagingRequest.Handler<IResp<List<T>>> handler) {
        return new Pager<T>(
                pageSize,
                isZeroStart,
                list,
                new DefaultPagingView<T>(list, smartRefreshLayout, adapter),
                handler,
                new DefaultPagingResponseHandler<T>()
        );
    }

    public static class DefaultPagingView<T> implements PagingView {

        private List<T> list;
        private SmartRefreshLayout smartRefreshLayout;
        private RecyclerView.Adapter adapter;

        public DefaultPagingView(List<T> list, SmartRefreshLayout smartRefreshLayout, RecyclerView.Adapter adapter) {
            this.list = list;
            this.smartRefreshLayout = smartRefreshLayout;
            this.adapter = adapter;
        }

        @Override
        public void bindView(Pager pager) {
            smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(RefreshLayout refreshLayout) {
                    pager.onLoadMore();
                }

                @Override
                public void onRefresh(RefreshLayout refreshLayout) {
                    pager.onRefresh();
                }
            });
        }

        @Override
        public void finishRefresh(int size) {
            adapter.notifyDataSetChanged();
            smartRefreshLayout.finishRefresh(size > 0);
        }

        @Override
        public void finishLoadMore(int size) {
            if (size > 0) {
                adapter.notifyItemRangeInserted(
                        list.size() - size,
                        size
                );
            }
            smartRefreshLayout.finishLoadMore(size > 0);
        }

        @Override
        public void setNoMore(boolean noMore) {
            smartRefreshLayout.setNoMoreData(noMore);
        }
    }

    public static class DefaultPagingResponseHandler<T> implements PagingResponseHandler<T, IResp<List<T>>> {

        private IResp<List<T>> lastResp;

        @Override
        public List<T> onResponse(IResp<List<T>> listIResp) {
            this.lastResp = listIResp;
            return listIResp.data();
        }

        @Override
        public boolean hasMore() {
            if (lastResp == null) return false;
            return lastResp.data() != null && !lastResp.data().isEmpty();
        }
    }
}
