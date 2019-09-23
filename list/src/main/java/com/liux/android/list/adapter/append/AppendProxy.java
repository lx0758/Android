package com.liux.android.list.adapter.append;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.liux.android.list.holder.MarginHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public class AppendProxy<T> {
    private static final int ITEM_VIEW_TYPE_HEADER = -10;
    private static final int ITEM_VIEW_TYPE_FOOTER = -20;

    private IAppend mIAppend;

    private List<MarginHolder> mHeaders = new ArrayList<>();
    private List<MarginHolder> mFooters = new ArrayList<>();

    public AppendProxy(IAppend iAppend) {
        mIAppend = iAppend;
    }

    public boolean isAppendPosition(int position) {
        return getAppendPositionType(position) != -1;
    }

    public int getAppendPositionType(int position) {
        if (position < mHeaders.size()) return ITEM_VIEW_TYPE_HEADER;
        if (position >= mIAppend.getItemCount() - mFooters.size()) return ITEM_VIEW_TYPE_FOOTER;
        return -1;
    }

    public boolean isAppendType(int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE_HEADER:
            case ITEM_VIEW_TYPE_FOOTER:
                return true;
        }
        return false;
    }

    public RecyclerView.ViewHolder getAppendTypeHolder(int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE_HEADER:
                return mHeaders.get(0);
            case ITEM_VIEW_TYPE_FOOTER:
                return mFooters.get(0);
        }
        return null;
    }

    public int getAppendItemCount() {
        return mHeaders.size() + mFooters.size();
    }

    public int getRealPosition(int position) {
        return position - mHeaders.size();
    }

    public int getShamPosition(int position) {
        return position + mHeaders.size();
    }

    public void setHeader(View view) {
        if (mHeaders.isEmpty()) {
            mHeaders.add(0, new MarginHolder(view));
            mIAppend.notifyItemInserted(0);
        } else {
            mHeaders.set(0, new MarginHolder(view));
            mIAppend.notifyItemChanged(0);
        }
    }

    public void setFooter(View view) {
        if (mFooters.isEmpty()) {
            mFooters.add(0, new MarginHolder(view));
            mIAppend.notifyItemInserted(mIAppend.getItemCount() - 1);
        } else {
            mFooters.set(0, new MarginHolder(view));
            mIAppend.notifyItemChanged(mIAppend.getItemCount() - 1);
        }
    }

    public boolean isHeaderPosition(int position) {
        return position >= 0 && position < mHeaders.size();
    }

    public boolean isFooterPosition(int position) {
        return position >= mIAppend.getItemCount() - mFooters.size() &&
                position < mIAppend.getItemCount();
    }

    /**
     * 适配当 RecyclerView.LayoutManager() 为 GridLayoutManager()
     * @param recyclerView 目标
     */
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = (GridLayoutManager) manager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isAppendPosition(position)) {
                        return gridManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    /**
     * 适配当 RecyclerView.LayoutManager() 为 StaggeredGridLayoutManager()
     * @param holder 目标
     */
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (isAppendPosition(holder.getAdapterPosition())) {
            ViewGroup.LayoutParams lp_vg = holder.itemView.getLayoutParams();
            if(lp_vg instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp_sglm = (StaggeredGridLayoutManager.LayoutParams) lp_vg;
                lp_sglm.setFullSpan(true);
            }
        }
    }
}
