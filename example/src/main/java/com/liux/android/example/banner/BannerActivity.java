package com.liux.android.example.banner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.liux.android.banner.BannerAdapter;
import com.liux.android.banner.BannerHolder;
import com.liux.android.banner.BannerView;
import com.liux.android.banner.DefaultIndicator;
import com.liux.android.example.R;
import com.liux.android.glide.GlideApp;
import com.liux.android.tool.TT;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liux on 2017/11/28.
 */

public class BannerActivity extends AppCompatActivity {

    private List<String> mBanners;
    private BannerView mBannerView;
    private BannerAdapter<String> mBannerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_banner);

        mBannerView = (BannerView) findViewById(R.id.vp_banner);

        mBanners = new ArrayList<String>();
        mBannerView.setScrollerTime(400);
        mBannerAdapter = new BannerAdapter<String>(mBanners, R.layout.item_banner) {
            @Override
            public void onBindData(BannerHolder holder, final String s, int index) {
                ImageView imageView = holder.getView(R.id.iv_image);
                if (s == null && s.length() == 0) {
                    imageView.setImageResource(R.drawable.background);
                } else {
                    GlideApp.with(imageView.getContext())
                            .asBitmap()
                            .load(s)
                            .into(imageView);
                }
                holder
                        .setText(R.id.tv_text, s)
                        .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TT.show(BannerActivity.this, "点击了Banner:" + s, TT.LENGTH_SHORT);
                    }
                });
            }
        };
        mBannerView.setAdapter(mBannerAdapter);
        mBannerView.setIndicator(new DefaultIndicator(this, R.drawable.indicator_bg));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Observable.fromArray(1L, 2L, 3L, 4L, 5L, 6L)
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(Long aLong) throws Exception {
                        return "https://6xyun.cn/templates/themes/default/static/img/rand/" + aLong + ".jpg";
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        mBanners.clear();
                        mBanners.addAll(strings);
                        mBannerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
    }
}
