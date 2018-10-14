package com.liux.android.example.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.liux.android.boxing.BoxingGlideLoader;
import com.liux.android.boxing.BoxingTool;
import com.liux.android.boxing.BoxingUcrop;
import com.liux.android.boxing.OnSingleSelectListener;
import com.liux.android.example.R;
import com.liux.android.qrcode.QRCodeDecoder;
import com.liux.android.qrcode.QRCodeScanningActivity;
import com.liux.android.tool.ActivityStarter;
import com.liux.android.tool.TT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class QRCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);

        /* 初始化Boxing */
        BoxingCrop.getInstance().init(new BoxingUcrop());
        BoxingMediaLoader.getInstance().init(new BoxingGlideLoader());
    }

    @OnClick({R.id.btn_default_scan, R.id.btn_customize_scan, R.id.btn_bitmap_decode, R.id.btn_bytes_decode, R.id.btn_file_decode, R.id.btn_file_descriptor_decode, R.id.btn_input_stream_decode, R.id.btn_generate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_default_scan:
                ActivityStarter.startActivityForResult(
                        this,
                        new Intent(this, QRCodeScanningActivity.class),
                        new ActivityStarter.Callback() {
                            @Override
                            public void onActivityResult(int resultCode, Intent data) {
                                if (resultCode != RESULT_OK) return;
                                TT.show(
                                        QRCodeActivity.this,
                                        QRCodeScanningActivity.resolveQRCode(data),
                                        TT.LENGTH_SHORT
                                );
                            }
                        }
                );
                break;
            case R.id.btn_customize_scan:
                startActivity(
                        new Intent(this, QRCodeCustomizeScanningActivity.class)
                );
                break;
            case R.id.btn_bitmap_decode:
                // FIXME: memory overflow may occur here
                BoxingTool.startSingle(this, true, false, new OnSingleSelectListener() {
                    @Override
                    public void onSingleSelect(ImageMedia imageMedia) {
                        File file = new File(imageMedia.getPath());

                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inPreferredConfig = Bitmap.Config.RGB_565;
                        opts.inJustDecodeBounds = false;
                        opts.inSampleSize = 1;
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

                        String result = QRCodeDecoder.decode(bitmap);
                        if (bitmap != null) bitmap.recycle();
                        TT.show(QRCodeActivity.this, result != null ? ("解码成功:\n" + result) : "解码失败", TT.LENGTH_SHORT);
                    }
                });
                break;
            case R.id.btn_bytes_decode:
                BoxingTool.startSingle(this, true, false, new OnSingleSelectListener() {
                    @Override
                    public void onSingleSelect(ImageMedia imageMedia) {
                        File file = new File(imageMedia.getPath());

                        FileInputStream fileInputStream = null;
                        ByteArrayOutputStream byteArrayOutputStream = null;
                        try {
                            fileInputStream = new FileInputStream(file);
                            byteArrayOutputStream = new ByteArrayOutputStream();

                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = fileInputStream.read(buffer)) > -1 ) {
                                byteArrayOutputStream.write(buffer, 0, len);
                            }

                            String result = QRCodeDecoder.decode(byteArrayOutputStream.toByteArray());
                            TT.show(QRCodeActivity.this, result != null ? ("解码成功:\n" + result) : "解码失败", TT.LENGTH_SHORT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fileInputStream != null) fileInputStream.close();
                                if (byteArrayOutputStream != null) byteArrayOutputStream.close();
                            } catch (Exception ignore) {}
                        }
                    }
                });
                break;
            case R.id.btn_file_decode:
                BoxingTool.startSingle(this, true, false, new OnSingleSelectListener() {
                    @Override
                    public void onSingleSelect(ImageMedia imageMedia) {
                        File file = new File(imageMedia.getPath());

                        String result = QRCodeDecoder.decode(file);
                        TT.show(QRCodeActivity.this, result != null ? ("解码成功:\n" + result) : "解码失败", TT.LENGTH_SHORT);
                    }
                });
                break;
            case R.id.btn_file_descriptor_decode:
                BoxingTool.startSingle(this, true, false, new OnSingleSelectListener() {
                    @Override
                    public void onSingleSelect(ImageMedia imageMedia) {
                        File file = new File(imageMedia.getPath());

                        FileInputStream fileInputStream = null;
                        try {
                            fileInputStream = new FileInputStream(file);

                            String result = QRCodeDecoder.decode(fileInputStream.getFD());
                            TT.show(QRCodeActivity.this, result != null ? ("解码成功:\n" + result) : "解码失败", TT.LENGTH_SHORT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fileInputStream != null) fileInputStream.close();
                            } catch (Exception ignore) {}
                        }
                    }
                });
                break;
            case R.id.btn_input_stream_decode:
                BoxingTool.startSingle(this, true, false, new OnSingleSelectListener() {
                    @Override
                    public void onSingleSelect(ImageMedia imageMedia) {
                        File file = new File(imageMedia.getPath());

                        InputStream inputStream = null;
                        FileInputStream fileInputStream = null;
                        ByteArrayOutputStream byteArrayOutputStream = null;
                        try {
                            fileInputStream = new FileInputStream(file);
                            byteArrayOutputStream = new ByteArrayOutputStream();

                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = fileInputStream.read(buffer)) > -1 ) {
                                byteArrayOutputStream.write(buffer, 0, len);
                            }

                            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

                            String result = QRCodeDecoder.decode(inputStream);
                            TT.show(QRCodeActivity.this, result != null ? ("解码成功:\n" + result) : "解码失败", TT.LENGTH_SHORT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (inputStream != null) inputStream.close();
                                if (fileInputStream != null) fileInputStream.close();
                                if (byteArrayOutputStream != null) byteArrayOutputStream.close();
                            } catch (Exception ignore) {}
                        }
                    }
                });
                break;
            case R.id.btn_generate:
                startActivity(
                        new Intent(this, QRCodeGenerateActivity.class)
                );
                break;
        }
    }
}
