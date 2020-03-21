package com.liux.android.list.adapter.append;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public interface IAppendAdapter<T, R extends RecyclerView.Adapter> {

    /**
     * 设置页眉布局
     * @param view View
     */
    R setHeader(View view);

    /**
     * 设置页脚布局
     * @param view View
     */
    R setFooter(View view);

    /**
     * 获取除去 Header/Footer 之后真实的位置
     * @param position 绘图定位
     * @return 真实定位
     */
    int getRealPosition(int position);

    /**
     * 获取包含 Header/Footer 之后真实的位置
     * @param position 绘图定位
     * @return 真实定位
     */
    int getShamPosition(int position);

    /**
     * 检查是否是页眉布局
     * @param position 绘图定位
     * @return 是否是页眉布局
     */
    boolean isHeaderPosition(int position);

    /**
     * 检查是否是页脚布局
     * @param position 绘图定位
     * @return 是否是页脚布局
     */
    boolean isFooterPosition(int position);
}
