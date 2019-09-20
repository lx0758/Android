package com.liux.android.list.holder;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 一个缓存全部条目控件的灵活的ViewHolder
 */
public class SuperHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mItemViews;

    public SuperHolder(View itemView) {
        super(itemView);
        mItemViews = new SparseArray<>();
    }

    public <T extends View>T getItemView() {
        return (T) itemView;
    }

    public <T extends View>T getView(@IdRes int id){
        View view = mItemViews.get(id);
        if(view == null){
            view = itemView.findViewById(id);
            mItemViews.put(id,view);
        }
        return (T) view;
    }

    public SuperHolder setText(@IdRes int id, CharSequence text) {
        ((TextView) getView(id)).setText(text);
        return this;
    }

    public SuperHolder setText(@IdRes int id, @StringRes int text) {
        ((TextView) getView(id)).setText(text);
        return this;
    }

    public SuperHolder setTextColor(@IdRes int id, int color) {
        ((TextView) getView(id)).setTextColor(color);
        return this;
    }

    public SuperHolder setTextColor(@IdRes int id, ColorStateList colors) {
        ((TextView) getView(id)).setTextColor(colors);
        return this;
    }

    public SuperHolder setHintTextColor(@IdRes int id, int color) {
        ((TextView) getView(id)).setHintTextColor(color);
        return this;
    }

    public SuperHolder setLinkTextColor(@IdRes int id, int color) {
        ((TextView) getView(id)).setLinkTextColor(color);
        return this;
    }

    public SuperHolder setHighlightColor(@IdRes int id, int color) {
        ((TextView) getView(id)).setHighlightColor(color);
        return this;
    }

    public SuperHolder setImageURI(@IdRes int id, Uri uri) {
        ((ImageView) getView(id)).setImageURI(uri);
        return this;
    }

    public SuperHolder setImageBitmap(@IdRes int id, Bitmap bitmap) {
        ((ImageView) getView(id)).setImageBitmap(bitmap);
        return this;
    }

    public SuperHolder setImageDrawable(@IdRes int id, Drawable drawable) {
        ((ImageView) getView(id)).setImageDrawable(drawable);
        return this;
    }

    public SuperHolder setImageResource(@IdRes int id, @DrawableRes int resid) {
        ((ImageView) getView(id)).setImageResource(resid);
        return this;
    }

    public SuperHolder setBackground(@IdRes int id, Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getView(id).setBackground(background);
        } else {
            getView(id).setBackgroundDrawable(background);
        }
        return this;
    }

    public SuperHolder setBackgroundColor(@IdRes int id, int color) {
        getView(id).setBackgroundColor(color);
        return this;
    }

    @Deprecated
    public SuperHolder setBackgroundDrawable(@IdRes int id, Drawable background) {
        getView(id).setBackgroundDrawable(background);
        return this;
    }

    public SuperHolder setBackgroundResource(@IdRes int id, @DrawableRes int resid) {
        getView(id).setBackgroundResource(resid);
        return this;
    }

    public SuperHolder setVisibility(@IdRes int id, int visibility) {
        getView(id).setVisibility(visibility);
        return this;
    }

    public SuperHolder setOnClickListener(View.OnClickListener listener) {
        getItemView().setOnClickListener(listener);
        return this;
    }

    public SuperHolder setOnClickListener(@IdRes int id, View.OnClickListener listener) {
        getView(id).setOnClickListener(listener);
        return this;
    }

    public SuperHolder setOnLongClickListener(View.OnLongClickListener listener) {
        getItemView().setOnLongClickListener(listener);
        return this;
    }

    public SuperHolder setOnLongClickListener(@IdRes int id, View.OnLongClickListener listener) {
        getView(id).setOnLongClickListener(listener);
        return this;
    }
}