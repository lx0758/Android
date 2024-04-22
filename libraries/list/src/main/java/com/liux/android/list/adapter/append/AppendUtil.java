package com.liux.android.list.adapter.append;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class AppendUtil {

    /**
     * 适配当 RecyclerView.LayoutManager() 为 GridLayoutManager()
     * @param recyclerView 目标
     */
    public static void onAttachedToRecyclerView(RecyclerView recyclerView, final AppendProxy appendProxy) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = (GridLayoutManager) manager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (appendProxy.isAppendPosition(position)) {
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
    public static void onViewAttachedToWindow(RecyclerView.ViewHolder holder, AppendProxy appendProxy) {
        if (appendProxy.isAppendPosition(holder.getAdapterPosition())) {
            if(holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            }
        }
    }
}
