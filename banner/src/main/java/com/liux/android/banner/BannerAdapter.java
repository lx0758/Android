package com.liux.android.banner;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * ViewPager里面对每个页面的管理是key-value形式的，也就是说每个page都有个对应的id（id是object类型），
 * 需要对page操作的时候都是通过id来完成的
 * https://segmentfault.com/q/1010000000484617
 * Created by Liux on 2017/8/28.
 */

public abstract class BannerAdapter<T> extends PagerAdapter {

    private int mLayoutRes;
    private List<T> mDataSource;

    public BannerAdapter(List<T> data, @LayoutRes int layout) {
        mLayoutRes = layout;
        mDataSource = data;
    }

    /**
     * 获取数据数量
     * @return
     */
    @Override
    public int getCount() {
        // 设置为 Integer.MAX_VALUE 会出现ANR的问题(也不能过大,0xFFFFFF都会偶现)
        return mDataSource == null ? 0 : 0xFFFF;
    }

    /**
     * 初始化Item
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (getRealCount() == 0) return new Object();

        BannerHolder holder = BannerHolder.create(container, mLayoutRes);
        container.addView(holder.getItemView());

        int index = position % getRealCount();

        // 必须再 addView() 之后调用,否则取尺寸有可能会为0
        onBindData(holder, mDataSource.get(index), index);

        return holder;
    }

    /**
     * 判断是否是要显示的数据
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        if (!(object instanceof BannerHolder)) return view == object;

        BannerHolder holder = (BannerHolder) object;
        return view == holder.getItemView();
    }

    /**
     * 当Item超出ViewPager缓存范围时,将调用此方法销毁View
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (!(object instanceof BannerHolder)) return;

        BannerHolder holder = (BannerHolder) object;
        container.removeView(holder.getItemView());
    }

    /**
     * 获取数据真实的数量
     * @return
     */
    public int getRealCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }

    public abstract void onBindData(BannerHolder holder, T t, int index);
}
