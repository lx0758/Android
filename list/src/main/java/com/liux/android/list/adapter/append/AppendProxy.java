package com.liux.android.list.adapter.append;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.liux.android.list.holder.MarginHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public class AppendProxy<T, R extends RecyclerView.Adapter> implements IAppendAdapter<T, R> {
    private static final int ITEM_VIEW_TYPE_HEADER = -10;
    private static final int ITEM_VIEW_TYPE_FOOTER = -20;

    private R mAdapter;

    private List<MarginHolder> mHeaders = new ArrayList<>();
    private List<MarginHolder> mFooters = new ArrayList<>();

    public AppendProxy(R adapter) {
        mAdapter = adapter;
    }

    @Override
    public R setHeader(View view) {
        if (mHeaders.isEmpty()) {
            mHeaders.add(0, new MarginHolder(view));
            mAdapter.notifyItemInserted(0);
        } else {
            mHeaders.set(0, new MarginHolder(view));
            mAdapter.notifyItemChanged(0);
        }
        return mAdapter;
    }

    @Override
    public R setFooter(View view) {
        if (mFooters.isEmpty()) {
            mFooters.add(0, new MarginHolder(view));
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        } else {
            mFooters.set(0, new MarginHolder(view));
            mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
        }
        return mAdapter;
    }

    @Override
    public boolean isHeaderPosition(int position) {
        return position >= 0 && position < mHeaders.size();
    }

    @Override
    public boolean isFooterPosition(int position) {
        return position >= mAdapter.getItemCount() - mFooters.size() &&
                position < mAdapter.getItemCount();
    }

    public boolean isAppendPosition(int position) {
        return getAppendPositionType(position) != -1;
    }

    public boolean isAppendType(int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE_HEADER:
            case ITEM_VIEW_TYPE_FOOTER:
                return true;
        }
        return false;
    }

    public int getAppendPositionType(int position) {
        if (position < mHeaders.size()) return ITEM_VIEW_TYPE_HEADER;
        if (position >= mAdapter.getItemCount() - mFooters.size()) return ITEM_VIEW_TYPE_FOOTER;
        return -1;
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

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        AppendUtil.onAttachedToRecyclerView(recyclerView, this);
    }

    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        AppendUtil.onViewAttachedToWindow(holder, this);
    }
}
