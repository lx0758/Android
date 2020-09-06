package com.liux.android.example.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.R;
import com.liux.android.mediaer.Mediaer;
import com.liux.android.mediaer.MediaerException;
import com.liux.android.mediaer.listener.OnSingleSelectListener;
import com.liux.android.qrcode.QRCodeDecoder;
import com.liux.android.qrcode.QRCodeScanningActivity;
import com.liux.android.tool.ActivityStarter;
import com.liux.android.tool.TT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class QRCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrcode);

        findViewById(R.id.btn_default_scan).setOnClickListener(this::onViewClicked);
        findViewById(R.id.btn_customize_scan).setOnClickListener(this::onViewClicked);
        findViewById(R.id.btn_bitmap_decode).setOnClickListener(this::onViewClicked);
        findViewById(R.id.btn_bytes_decode).setOnClickListener(this::onViewClicked);
        findViewById(R.id.btn_file_decode).setOnClickListener(this::onViewClicked);
        findViewById(R.id.btn_file_descriptor_decode).setOnClickListener(this::onViewClicked);
        findViewById(R.id.btn_input_stream_decode).setOnClickListener(this::onViewClicked);
        findViewById(R.id.btn_generate).setOnClickListener(this::onViewClicked);
    }

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
                                TT.show(QRCodeScanningActivity.resolveQRCode(data));
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
                Mediaer.with(this)
                        .singleSelect()
                        .listener(new OnSingleSelectListener() {
                            @Override
                            public void onFailure(MediaerException e) {
                                TT.show("出错了!");
                            }

                            @Override
                            public void onSingleSelect(Uri uri) {
                                File file = new File(uri.getPath());

                                BitmapFactory.Options opts = new BitmapFactory.Options();
                                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                                opts.inJustDecodeBounds = false;
                                opts.inSampleSize = 1;
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

                                String result = QRCodeDecoder.decode(bitmap);
                                if (bitmap != null) bitmap.recycle();
                                TT.show(result != null ? ("解码成功:\n" + result) : "解码失败");
                            }
                        })
                        .start();
                break;
            case R.id.btn_bytes_decode:
                Mediaer.with(this)
                        .singleSelect()
                        .listener(new OnSingleSelectListener() {
                            @Override
                            public void onFailure(MediaerException e) {
                                TT.show("出错了!");
                            }

                            @Override
                            public void onSingleSelect(Uri uri) {
                                File file = new File(uri.getPath());

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
                                    TT.show(result != null ? ("解码成功:\n" + result) : "解码失败");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (fileInputStream != null) fileInputStream.close();
                                        if (byteArrayOutputStream != null) byteArrayOutputStream.close();
                                    } catch (Exception ignore) {}
                                }
                            }
                        })
                        .start();
                break;
            case R.id.btn_file_decode:
                Mediaer.with(this)
                        .singleSelect()
                        .listener(new OnSingleSelectListener() {
                            @Override
                            public void onFailure(MediaerException e) {
                                TT.show("出错了!");
                            }

                            @Override
                            public void onSingleSelect(Uri uri) {
                                File file = new File(uri.getPath());

                                String result = QRCodeDecoder.decode(file);
                                TT.show(result != null ? ("解码成功:\n" + result) : "解码失败");
                            }
                        })
                        .start();
                break;
            case R.id.btn_file_descriptor_decode:
                Mediaer.with(this)
                        .singleSelect()
                        .listener(new OnSingleSelectListener() {
                            @Override
                            public void onFailure(MediaerException e) {
                                TT.show("出错了!");
                            }

                            @Override
                            public void onSingleSelect(Uri uri) {
                                File file = new File(uri.getPath());

                                FileInputStream fileInputStream = null;
                                try {
                                    fileInputStream = new FileInputStream(file);

                                    String result = QRCodeDecoder.decode(fileInputStream.getFD());
                                    TT.show(result != null ? ("解码成功:\n" + result) : "解码失败");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (fileInputStream != null) fileInputStream.close();
                                    } catch (Exception ignore) {}
                                }
                            }
                        })
                        .start();
                break;
            case R.id.btn_input_stream_decode:
                Mediaer.with(this)
                        .singleSelect()
                        .listener(new OnSingleSelectListener() {
                            @Override
                            public void onFailure(MediaerException e) {
                                TT.show("出错了!");
                            }

                            @Override
                            public void onSingleSelect(Uri uri) {
                                File file = new File(uri.getPath());

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
                                    TT.show(result != null ? ("解码成功:\n" + result) : "解码失败");
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
                        })
                        .start();
                break;
            case R.id.btn_generate:
                startActivity(
                        new Intent(this, QRCodeGenerateActivity.class)
                );
                break;
        }
    }
}
