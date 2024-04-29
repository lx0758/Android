package com.liux.android.example.qrcode;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.liux.android.example.R;
import com.liux.android.example.databinding.ActivityQrcodeGenerateBinding;
import com.liux.android.multimedia.glide.GlideApp;
import com.liux.android.qrcode.QRCodeEncoder;
import com.liux.android.tool.TT;

public class QRCodeGenerateActivity extends AppCompatActivity {

    private ActivityQrcodeGenerateBinding mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityQrcodeGenerateBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.btnGenerate.setOnClickListener(view -> {
            GlideApp.with(mViewBinding.ivCode)
                    .load(QRCodeEncoder.encode(
                            mViewBinding.etContent.getText().toString(),
                            mViewBinding.ivCode.getWidth(),
                            mViewBinding.ivCode.getHeight(),
                            mViewBinding.cbLogo.isChecked() ? BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) : null
                    ))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            TT.show("二维码生成失败");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            TT.show("二维码生成成功");
                            return false;
                        }
                    })
                    .into(mViewBinding.ivCode);
        });
    }
}
