package com.liux.android.mediaer.action.preview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;
import androidx.viewpager.widget.ViewPager;

import com.liux.android.mediaer.R;

import java.util.List;
import java.util.Locale;

public class PreviewDialog extends AppCompatDialog {

    private List<Uri> medias;
    private int position;

    private ViewPager vpContent;
    private TextView tvIndicator;

    public PreviewDialog(Context context, List<Uri> medias, int position) {
        super(context);
        this.medias = medias;
        this.position = position;

        getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_preview);

        vpContent = findViewById(R.id.vp_content);
        tvIndicator = findViewById(R.id.tv_indicator);

        vpContent.setAdapter(new PreviewAdapter(medias, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        }));
        vpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onRefreshIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vpContent.setCurrentItem(position);
        onRefreshIndicator(position);
    }

    private void onRefreshIndicator(int position) {
        tvIndicator.setText(String.format(Locale.CHINA, "%d/%d", position + 1, medias.size()));
    }
}
