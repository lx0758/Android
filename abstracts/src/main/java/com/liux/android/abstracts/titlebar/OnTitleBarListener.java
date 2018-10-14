package com.liux.android.abstracts.titlebar;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

public interface OnTitleBarListener {

    /**
     * 返回事件触发 <br>
     * @return 是否已处理,否则调用 {@link android.app.Activity#onBackPressed()}
     */
    boolean onBack();

    /**
     * 更多事件处理 <br>
     * @return 是否已处理
     */
    boolean onMore();
}
