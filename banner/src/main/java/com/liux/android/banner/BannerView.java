package com.liux.android.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Banner View 实现
 * Created by Liux on 2017/8/28.
 */

public class BannerView extends RelativeLayout {
    private static final int MESSAGE_WHAT = 1;

    private static final int INDEX_VIEW_PAGER = 0;
    private static final int INDEX_INDICATOR = 1;

    // 切换间隔时间
    private int mInterval = 3000;
    // Banner
    private ViewPager mViewPager;
    // Banner 指示器
    private Indicator mIndicator;
    // 无限循环适配器
    private BannerAdapter mBannerAdapter;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mBannerAdapter == null || mBannerAdapter.getRealCount() == 0) {
                return;
            }
            if (mViewPager != null) {
                int position = mViewPager.getCurrentItem();
                if (position < mBannerAdapter.getCount()) {
                    mViewPager.setCurrentItem(position + 1);
                } else {
                    position = position / 2;
                    position = position - position % mBannerAdapter.getRealCount();
                    mViewPager.setCurrentItem(position);
                }
            }
            mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, mInterval);
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mIndicator != null) {
                int real_position = 0;
                if (mBannerAdapter.getRealCount() > 1) {
                    real_position = position % mBannerAdapter.getRealCount();
                }
                mIndicator.onSelected(BannerView.this, real_position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            onInvalidated();

            // ViewPager 缓存了子页面信息,通过这种方式清除
            mViewPager.setAdapter(mBannerAdapter);

            int count = mBannerAdapter.getCount();
            int real_count = mBannerAdapter.getRealCount();
            if (count > 0 && real_count > 0) {
                // 取最大值的中间位置,并确保证显示第一个页
                int position = count / 2;
                position = position - position % real_count;
                mViewPager.setCurrentItem(position);

                if (mIndicator != null) {
                    mIndicator.onInit(BannerView.this, real_count);
                }

                if (real_count > 1) {
                    mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, mInterval);
                }
            }
        }

        @Override
        public void onInvalidated() {
            mHandler.removeMessages(MESSAGE_WHAT);

            mViewPager.removeAllViews();
            if (mIndicator != null) {
                mIndicator.onClear(BannerView.this);
            }
        }
    };

    public BannerView(Context context) {
        super(context);
        init();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mViewPager = new ViewPager(getContext());

        addView(mViewPager, INDEX_VIEW_PAGER, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(MESSAGE_WHAT);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mBannerAdapter != null && mBannerAdapter.getRealCount() > 0) {
                    mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, mInterval);
                }
                break;

        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_WHAT);
        }
    }

    public int getInterval() {
        return mInterval;
    }

    public void setInterval(int interval) {
        // 间隔不能小于200ms
        if (mInterval < 200) mInterval = 200;
        mInterval = interval;
    }

    public BannerAdapter getAdapter() {
        return mBannerAdapter;
    }

    public void setAdapter(BannerAdapter adapter) {
        if (mBannerAdapter != null) {
            mBannerAdapter.unregisterDataSetObserver(mDataSetObserver);
            mHandler.removeMessages(MESSAGE_WHAT);
        }

        mBannerAdapter = adapter;
        mViewPager.setAdapter(mBannerAdapter);

        if (mBannerAdapter != null) mBannerAdapter.registerDataSetObserver(mDataSetObserver);
    }

    public void setIndicator(Indicator indicator) {
        if (mIndicator != null) {
            mIndicator.onClear(this);

            View view = getChildAt(INDEX_INDICATOR);
            if (view instanceof Indicator) {
                removeView(view);
            }
        }
        mIndicator = indicator;
        if (mIndicator != null) {
            View view = mIndicator.onBind(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(view, INDEX_INDICATOR, layoutParams);
        }
    }

    /**
     * 设置 ViewPager 滚动时间
     * @param time
     */
    public void setScrollerTime(int time) {
        // 防止滚动异常
        if (time > mInterval) time = mInterval;
        setScroller(new BannerScroller(getContext()).setDuration(time));
    }

    /**
     * 设置 ViewPager 滚动插值器
     * @param scroller
     */
    public void setScroller(Scroller scroller) {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mField.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
