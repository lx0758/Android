package com.liux.android.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * date：2018/11/28 11:18
 * author：Liux
 * email：lx0758@qq.com
 * description：适配内嵌在 ScrollView 时显示不全和滑动有阻力问题
 */
public class ScrollRecyclerView extends RecyclerView {

    public ScrollRecyclerView(@NonNull Context context) {
        super(context);
    }

    public ScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int height = MeasureSpec.getSize(heightSpec);
        int heightMode = MeasureSpec.getMode(heightSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        } else {
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 避免滑动阻滞
        setNestedScrollingEnabled(false);
    }
}
