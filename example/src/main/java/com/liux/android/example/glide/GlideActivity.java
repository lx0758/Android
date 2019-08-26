package com.liux.android.example.glide;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.R;
import com.liux.android.glide.GlideApp;
import com.liux.android.glide.video.Video;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/11/28.
 */

public class GlideActivity extends AppCompatActivity {
    private static final String TEST_URL = "http://html.6xyun.cn/media/avi";

    @BindView(R.id.iv_image)
    ImageView ivImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_glide);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_self, R.id.btn_realize})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_self:
                GlideApp.with(ivImage)
                        .asBitmap()
                        .load(TEST_URL)
                        .into(ivImage);
                break;
            case R.id.btn_realize:
                GlideApp.with(ivImage)
                        .asBitmap()
                        .load(Video.from(TEST_URL))
                        .into(ivImage);
                break;
        }
    }
}
