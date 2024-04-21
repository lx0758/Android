package com.liux.android.list.holder;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Liux on 2017/9/19.
 */

public class MarginHolder extends RecyclerView.ViewHolder {

    public MarginHolder(View itemView) {
        super(itemView);

        ViewParent viewParent = itemView.getParent();
        if (viewParent instanceof ViewGroup) {
            ((ViewGroup) viewParent).removeView(itemView);
        }
    }
}
