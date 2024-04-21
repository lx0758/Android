package com.liux.android.banner;

import android.view.View;

/**
 * Created by Liux on 2017/9/1.
 */

public interface Indicator {

    View onBind(BannerView bannerView);

    void onInit(BannerView view, int count);

    void onSelected(BannerView view, int position);

    void onClear(BannerView view);
}
