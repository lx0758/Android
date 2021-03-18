package com.liux.android.framework.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * 2018/3/1
 * By Liux
 * lx0758@qq.com
 */

public class GuideAdapter extends PagerAdapter {

    private List<View> mViews;

    public GuideAdapter(List<View> views) {
        this.mViews = views;

    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

}