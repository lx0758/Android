package com.liux.android.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MenuItem;

/**
 * 主页底部导航栏
 * 2018/3/2
 * By Liux
 * lx0758@qq.com
 */

public class TabNavigationView extends BottomNavigationView{

    private static final int[][] STATES = new int[][]{
            new int[]{android.R.attr.state_checked},
            EMPTY_STATE_SET
    };

    private ViewPager mViewPager;
    private NavigationViewOnPageChangeListener mNavigationViewOnPageChangeListener;
    private ViewPagerOnNavigationItemSelectedListener mViewPagerOnNavigationItemSelectedListener = new ViewPagerOnNavigationItemSelectedListener(this);

    public TabNavigationView(Context context) {
        super(context);
    }

    public TabNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置是否有位移切换效果
     * @param shiftMode
     */
    public void setShiftMode(boolean shiftMode) {
        // -1_自动 0_开启 1_关闭
        super.setLabelVisibilityMode(shiftMode ? 0 : 1);
    }

    /**
     * 设置 ICON 切换状态的尺寸
     * @param size
     */
    public void setItemIconSize(@Size float size) {
        setItemIconSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    /**
     * 设置 ICON 切换状态的尺寸
     * @param unit
     * @param size
     */
    public void setItemIconSize(int unit, @Size float size) {
        int int_size = (int) TypedValue.applyDimension(unit, size, getContext().getResources().getDisplayMetrics());
        super.setItemIconSize(int_size);
    }

    /**
     * 设置 ICON 切换状态的颜色
     * @param unchecked
     * @param checked
     */
    public void setItemIconTintList(@ColorInt int unchecked, @ColorInt int checked) {
        ColorStateList colorStateList = getColorStateList(unchecked, checked);
        super.setItemIconTintList(colorStateList);
    }

    /**
     * 设置 文本 切换状态的尺寸
     * @param unchecked
     * @param checked
     */
//    public void setItemTextSize(@Size float unchecked, @Size float checked) {
//        setItemTextSize(TypedValue.COMPLEX_UNIT_SP, unchecked, checked);
//    }

    /**
     * 设置 文本 切换状态的尺寸
     * @param unit
     * @param unchecked
     * @param checked
     */
//    public void setItemTextSize(int unit, @Size float unchecked, @Size float checked) {
//        int int_unchecked = (int) TypedValue.applyDimension(unit, unchecked, getContext().getResources().getDisplayMetrics());
//        int int_checked = (int) TypedValue.applyDimension(unit, checked, getContext().getResources().getDisplayMetrics());
//        super.setItemTextAppearanceActive();
//        super.setItemTextAppearanceInactive();
//    }

    /**
     * 设置 文本 切换状态的颜色
     * @param unchecked
     * @param checked
     */
    public void setItemTextColor(@ColorInt int unchecked, @ColorInt int checked) {
        ColorStateList colorStateList = getColorStateList(unchecked, checked);
        super.setItemTextColor(colorStateList);
    }

    /**
     * 设置选中某个条目
     * @param index
     */
    public void setSelectedItemIndex(int index) {
        int size = getMenu().size();
        if (index < 0 || index >= size) return;
        setSelectedItemId(getMenu().getItem(index).getItemId());
    }

    /**
     * 和 ViewPager 进行绑定
     * @param viewPager
     */
    public void setupWithViewPager(ViewPager viewPager) {
        if (mViewPager != null) {
            // If we've already been setup with a ViewPager, remove us from it
            if (mNavigationViewOnPageChangeListener != null) {
                mViewPager.removeOnPageChangeListener(mNavigationViewOnPageChangeListener);
            }

            if (mViewPagerOnNavigationItemSelectedListener != null) {
                // If we already have a tab selected listener for the ViewPager, remove it
                mViewPagerOnNavigationItemSelectedListener.setViewPager(null);
            }
        }

        if (viewPager != null) {
            mViewPager = viewPager;

            // Add our custom OnPageChangeListener to the ViewPager
            if (mNavigationViewOnPageChangeListener == null) {
                mNavigationViewOnPageChangeListener = new NavigationViewOnPageChangeListener(this);
            }
            viewPager.addOnPageChangeListener(mNavigationViewOnPageChangeListener);

            // Now we'll add a tab selected listener to set ViewPager's current item
            mViewPagerOnNavigationItemSelectedListener.setViewPager(viewPager);
            super.setOnNavigationItemSelectedListener(mViewPagerOnNavigationItemSelectedListener);
        } else {
            // We've been given a null ViewPager so we need to clear out the internal state,
            // listeners and observers
            mViewPager = null;
        }
    }

    @Override
    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        mViewPagerOnNavigationItemSelectedListener.setOnNavigationItemSelectedListener(listener);
    }

    private ColorStateList getColorStateList(int unchecked, int checked) {
        int[] colors = new int[]{checked, unchecked};
        return new ColorStateList(STATES, colors);
    }

    private static class NavigationViewOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private final TabNavigationView mTabNavigationView;

        public NavigationViewOnPageChangeListener(TabNavigationView tabNavigationView) {
            mTabNavigationView = tabNavigationView;
        }

        @Override
        public void onPageScrollStateChanged(final int state) {

        }

        @Override
        public void onPageScrolled(final int position, final float positionOffset,
                                   final int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
            if (mTabNavigationView != null) {
                mTabNavigationView.setSelectedItemIndex(position);
            }
        }
    }

    private static class ViewPagerOnNavigationItemSelectedListener implements OnNavigationItemSelectedListener {
        private TabNavigationView mTabNavigationView;

        private OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
        private ViewPager mViewPager;

        public ViewPagerOnNavigationItemSelectedListener(TabNavigationView tabNavigationView) {
            mTabNavigationView = tabNavigationView;
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int index = -1, size = mTabNavigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                if (mTabNavigationView.getMenu().getItem(i).getItemId() == item.getItemId()) {
                    index = i;
                    break;
                }
            }

            if (mOnNavigationItemSelectedListener != null &&
                    !mOnNavigationItemSelectedListener.onNavigationItemSelected(item)) {
                return false;
            }

            if (mViewPager != null && index != -1) {
                mViewPager.setCurrentItem(index);
                return true;
            }
            return false;
        }

        public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
            mOnNavigationItemSelectedListener = listener;
        }

        public void setViewPager(ViewPager viewPager) {
            mViewPager = viewPager;
        }
    }
}
