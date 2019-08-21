package com.liux.android.list.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Liux on 2017/9/19.
 */

public class MarginHolder extends RecyclerView.ViewHolder {

    public static MarginHolder create(ViewGroup parent, @LayoutRes int layout) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MarginHolder(view);
    }

    public MarginHolder(View itemView) {
        super(itemView);
        checkParent(itemView);
    }

    private void checkParent(View itemView) {
        ViewParent viewParent = itemView.getParent();
        if (viewParent != null && viewParent instanceof ViewGroup) {
            ((ViewGroup) viewParent).removeView(itemView);
        }
    }
}
