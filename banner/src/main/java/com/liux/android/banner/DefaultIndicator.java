package com.liux.android.banner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 默认的指示器实现 <br>
 * 暂时没有动效,比较生硬
 * Created by Liux on 2017/9/3.
 */

public class DefaultIndicator extends LinearLayout implements Indicator {
    // 指示器资源
    private int mIndicatorResource;

    /**
     * 资源使用选择器标识 <br>
     * android:state_selected="true" or android:state_selected="false"
     * @param context
     * @param res
     */
    public DefaultIndicator(Context context, @DrawableRes int res) {
        super(context);
        mIndicatorResource = res;
        init();
    }

    public DefaultIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setPadding(0, 0, 0, dp2px(getContext(), 12));
        setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
    }

    @Override
    public View onBind(BannerView bannerView) {
        return this;
    }

    @Override
    public void onInit(BannerView bannerView, int count) {
        removeAllViews();
        for (int i = 0; i < count; i++) {
            Drawable drawable = getResources().getDrawable(mIndicatorResource);
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            View view = new View(getContext());
            view.setBackgroundDrawable(drawable);
            LayoutParams lp = new LayoutParams(width, height);
            lp.setMargins(width / 4, 0, width / 4, 0);
            view.setLayoutParams(lp);
            addView(view);
        }
        if (count > 0) {
            getChildAt(0).setSelected(true);
        }
    }

    @Override
    public void onSelected(BannerView view, int position) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setSelected(i == position);
        }
    }

    @Override
    public void onClear(BannerView view) {
        removeAllViews();
    }

    /**
     * 设置指示器资源 <br>
     * 资源使用选择器标识 <br>
     * android:state_selected="true" or android:state_selected="false"
     * @param res
     */
    public void setIndicatorResource(@DrawableRes int res) {
        mIndicatorResource = res;

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.setBackgroundResource(mIndicatorResource);
        }
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
