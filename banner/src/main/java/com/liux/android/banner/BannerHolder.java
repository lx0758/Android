package com.liux.android.banner;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 一个缓存全部条目控件的灵活的ViewHolder
 */
public class BannerHolder {

    public static BannerHolder create(ViewGroup container, int layoutRes) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(layoutRes, container, false);
        return new BannerHolder(itemView);
    }

    private View mItemView;
    private SparseArray<View> mItemViews;

    private BannerHolder(View itemView) {
        mItemView = itemView;
        mItemViews = new SparseArray<>();
    }

    public <T extends View>T getItemView() {
        return (T) mItemView;
    }

    public <T extends View>T getView(@IdRes int id){
        View view = mItemViews.get(id);
        if(view == null){
            view = mItemView.findViewById(id);
            mItemViews.put(id,view);
        }
        return (T) view;
    }

    public BannerHolder setText(@IdRes int id, String text) {
        ((TextView) getView(id)).setText(text);
        return this;
    }

    public BannerHolder setText(@IdRes int id, @StringRes int text) {
        ((TextView) getView(id)).setText(text);
        return this;
    }

    public BannerHolder setTextColor(@IdRes int id, int color) {
        ((TextView) getView(id)).setTextColor(color);
        return this;
    }

    public BannerHolder setBackgroundColor(@IdRes int id, int color) {
        getView(id).setBackgroundColor(color);
        return this;
    }

    public BannerHolder setVisibility(@IdRes int id, int visibility) {
        getView(id).setVisibility(visibility);
        return this;
    }

    public BannerHolder setOnClickListener(View.OnClickListener listener) {
        getItemView().setOnClickListener(listener);
        return this;
    }

    public BannerHolder setOnClickListener(@IdRes int id, View.OnClickListener listener) {
        getView(id).setOnClickListener(listener);
        return this;
    }

    public BannerHolder setOnLongClickListener(View.OnLongClickListener listener) {
        getItemView().setOnLongClickListener(listener);
        return this;
    }

    public BannerHolder setOnLongClickListener(@IdRes int id, View.OnLongClickListener listener) {
        getView(id).setOnLongClickListener(listener);
        return this;
    }
}