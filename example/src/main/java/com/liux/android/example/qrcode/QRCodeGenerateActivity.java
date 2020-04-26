package com.liux.android.example.qrcode;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.liux.android.example.R;
import com.liux.android.mediaer.glide.GlideApp;
import com.liux.android.qrcode.QRCodeEncoder;
import com.liux.android.tool.TT;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QRCodeGenerateActivity extends AppCompatActivity {

    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.cb_logo)
    CheckBox cbLogo;
    @BindView(R.id.btn_generate)
    Button btnGenerate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrcode_generate);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_generate)
    public void onViewClicked() {
        GlideApp.with(ivCode)
                .load(QRCodeEncoder.encode(
                        etContent.getText().toString(),
                        ivCode.getWidth(),
                        ivCode.getHeight(),
                        cbLogo.isChecked() ? BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) : null
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
                .into(ivCode);
    }
}
