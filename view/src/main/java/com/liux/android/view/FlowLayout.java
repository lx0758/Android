package com.liux.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Liux on 2017/8/2.
 */

public class FlowLayout extends ViewGroup {

    // Tag之间的垂直间隙
    private int mVerticalInterval;
    // Tag之间的水平间隙
    private int mHorizontalInterval;


    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.FlowLayout, defStyleAttr, 0);

        mHorizontalInterval = array.getDimensionPixelSize(R.styleable.FlowLayout_flow_horizontal, 0);
        mVerticalInterval = array.getDimensionPixelSize(R.styleable.FlowLayout_flow_vertical, 0);

        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        // 计算可用宽度，为测量宽度减去左右padding值
        int availableWidth = widthSpecSize - getPaddingLeft() - getPaddingRight();
        // 测量子视图
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        int tmpWidth = 0;
        int measureHeight = 0;
        int maxLineHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // 记录该行的最大高度
            if (maxLineHeight == 0) {
                maxLineHeight = child.getMeasuredHeight();
            } else {
                maxLineHeight = Math.max(maxLineHeight, child.getMeasuredHeight());
            }
            // 统计该行TagView的总宽度
            tmpWidth += child.getMeasuredWidth() + mHorizontalInterval;
            // 如果超过可用宽度则换行
            if (tmpWidth - mHorizontalInterval > availableWidth) {
                // 统计TagGroup的测量高度，要加上垂直间隙
                measureHeight += maxLineHeight + mVerticalInterval;
                // 重新赋值
                tmpWidth = child.getMeasuredWidth() + mHorizontalInterval;
                maxLineHeight = child.getMeasuredHeight();
            }
        }
        // 统计TagGroup的测量高度，加上最后一行
        measureHeight += maxLineHeight;

        // 设置测量宽高，记得算上padding
        if (childCount == 0) {
            setMeasuredDimension(0, 0);
        } else if (heightSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize, measureHeight + getPaddingTop() + getPaddingBottom());
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }

        int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        // 当前布局使用的top坐标
        int curTop = getPaddingTop();
        // 当前布局使用的left坐标
        int curLeft = getPaddingLeft();
        int maxHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (maxHeight == 0) {
                maxHeight = child.getMeasuredHeight();
            } else {
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }

            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            // 超过一行做换行操作
            if (width + curLeft > availableWidth) {
                curLeft = getPaddingLeft();
                // 计算top坐标，要加上垂直间隙
                curTop += maxHeight + mVerticalInterval;
                maxHeight = child.getMeasuredHeight();
            }
            // 设置子视图布局
            child.layout(curLeft, curTop, curLeft + width, curTop + height);
            // 计算left坐标，要加上水平间隙
            curLeft += width + mHorizontalInterval;
        }
    }
}
