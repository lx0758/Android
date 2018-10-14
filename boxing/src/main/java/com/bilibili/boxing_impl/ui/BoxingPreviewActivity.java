package com.bilibili.boxing_impl.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bilibili.boxing.AbsBoxingViewActivity;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing_impl.view.HackyViewPager;
import com.liux.android.boxing.R;

import java.util.ArrayList;
import java.util.List;

public class BoxingPreviewActivity extends AbsBoxingViewActivity {
    public static final String PARAM_IMAGES = "BoxingPreviewActivity_PARAM_IMAGES";
    public static final String PARAM_POS = "BoxingPreviewActivity_PARAM_POS";

    private int mPos;
    private boolean mFinishLoading;
    private ArrayList<BaseMedia> mImages;

    private TextView mHint;
    private ImagesAdapter mAdapter;
    private HackyViewPager mGallery;
    private ProgressBar mProgressBar;

    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openFullScreen();

        setContentView(R.layout.activity_boxing_preview);

        initData(savedInstanceState, getIntent());
        initView();

        startLoading();
    }

    @Override
    public void startLoading() {
        if (mImages == null || mImages.isEmpty()) {
            return;
        }
        mAdapter.notifyDataSetChanged();

        setupGallery();

        mHint.setText(getString(
                R.string.boxing_image_preview_title_fmt,
                String.valueOf(++mPos),
                String.valueOf(mImages.size())
        ));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mImages != null) {
            outState.putParcelableArrayList(PARAM_IMAGES, mImages);
        }
        outState.putInt(PARAM_POS, mPos);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void openFullScreen() {
        getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void initData(Bundle savedInstanceState, Intent intent) {
        if (savedInstanceState != null) {
            mImages = savedInstanceState.getParcelableArrayList(PARAM_IMAGES);
            mPos = savedInstanceState.getInt(PARAM_POS, 0);
        } else if (intent != null) {
            mImages = intent.getParcelableArrayListExtra(PARAM_IMAGES);
            mPos = intent.getIntExtra(PARAM_POS, 0);
        }
    }

    private void initView() {
        mAdapter = new ImagesAdapter(getSupportFragmentManager(), mImages);
        mHint = findViewById(R.id.title);
        mGallery = findViewById(R.id.pager);
        mProgressBar = findViewById(R.id.loading);
        mGallery.setAdapter(mAdapter);
        mGallery.addOnPageChangeListener(new OnPagerChangeListener());

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                finish();
                return true;
            }
        });
    }

    private void setupGallery() {
        if (mGallery == null || mPos < 0) {
            return;
        }
        if (mPos < mImages.size() && !mFinishLoading) {
            mGallery.setCurrentItem(mPos, false);
            mProgressBar.setVisibility(View.GONE);
            mGallery.setVisibility(View.VISIBLE);
            mFinishLoading = true;
        } else if (mPos >= mImages.size()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mGallery.setVisibility(View.GONE);
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private class ImagesAdapter extends FragmentStatePagerAdapter {
        private List<BaseMedia> mMedias;

        ImagesAdapter(FragmentManager fm, List<BaseMedia> medias) {
            super(fm);
            mMedias = medias;
        }

        @Override
        public Fragment getItem(int i) {
            return BoxingRawImageFragment.newInstance((ImageMedia) mMedias.get(i));
        }

        @Override
        public int getCount() {
            return mMedias == null ? 0 : mMedias.size();
        }

        public void setMedias(ArrayList<BaseMedia> medias) {
            this.mMedias = medias;
            notifyDataSetChanged();
        }
    }

    private class OnPagerChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            if (position < mImages.size()) {
                mHint.setText(getString(
                        R.string.boxing_image_preview_title_fmt,
                        String.valueOf(position + 1),
                        String.valueOf(mImages.size())
                ));
            }
        }
    }
}
