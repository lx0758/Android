package com.liux.android.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 提供一个解决嵌套在滚动视图(例如ScrollView)中无法撑开,并抢走焦点等问题的RecyclerView<br>
 * Created by Liux on 2017/11/14.
 */

public class AllRecyclerView extends RecyclerView {

    public AllRecyclerView(Context context) {
        this(context, null);
    }

    public AllRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // 清除焦点
        clearFocus();
        // 组织滚动
        setNestedScrollingEnabled(false);
        // 阻止自动抢占焦点
        ((ViewGroup) getParent()).setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
    }
}
