package com.liux.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * date：2018/2/24
 * author：Liux
 * email：lx0758@qq.com
 * description：可以自动画控件阴影的布局
 */

public class ShadowLayout extends FrameLayout {

    private int mSize = 0;
    private int mColor = 0x1E000000;

    private Rect mRect;
    private Paint mPaint;
    private BlurMaskFilter mBlurMaskFilter;

    public ShadowLayout(@NonNull Context context) {
        super(context);
        initView(null);
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @Override
    public void onViewAdded(View child) {
        MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
        layoutParams.setMargins(
                layoutParams.leftMargin + mSize,
                layoutParams.topMargin + mSize,
                layoutParams.rightMargin + mSize,
                layoutParams.bottomMargin + mSize
        );
        super.onViewAdded(child);
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mRect = new Rect(0, 0, right - left, bottom - top);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mBlurMaskFilter = new BlurMaskFilter(mSize, BlurMaskFilter.Blur.NORMAL);
        mPaint.setMaskFilter(mBlurMaskFilter);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSize == 0) return;

        Bitmap temp = getDrawingCache().extractAlpha();
        Rect rect = new Rect(0, 0, temp.getWidth(), temp.getHeight());
        canvas.drawBitmap(temp, rect, mRect, mPaint);
        temp.recycle();
    }

    private void initView(AttributeSet attrs) {
        setWillNotDraw(false);
        setDrawingCacheEnabled(true);
        setLayerType(LAYER_TYPE_SOFTWARE,null);

        if (attrs == null) return;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowLayout);

        mSize = typedArray.getDimensionPixelSize(R.styleable.ShadowLayout_sl_size, mSize);
        mColor = typedArray.getColor(R.styleable.ShadowLayout_sl_color, mColor);

        typedArray.recycle();
    }
}
