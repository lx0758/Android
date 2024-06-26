package com.liux.android.example.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.R;
import com.liux.android.example.databinding.ActivityPermissionBinding;
import com.liux.android.multimedia.Multimedia;
import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.glide.GlideApp;
import com.liux.android.multimedia.listener.OnTakeListener;
import com.liux.android.permission.Authorizer;
import com.liux.android.permission.Continue;
import com.liux.android.permission.OnContinueListener;
import com.liux.android.permission.floats.OnFloatPermissionListener;
import com.liux.android.permission.install.OnInstallPermissionListener;
import com.liux.android.permission.runtime.OnRuntimePermissionListener;
import com.liux.android.tool.TT;

import java.util.List;

/**
 * Created by Liux on 2017/11/28.
 */

public class PermissionActivity extends AppCompatActivity {

    private ActivityPermissionBinding mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.btnCall.setOnClickListener(this::onViewClicked);
        mViewBinding.btnCamera.setOnClickListener(this::onViewClicked);
        mViewBinding.btnCallCamera.setOnClickListener(this::onViewClicked);
        mViewBinding.btnInstall.setOnClickListener(this::onViewClicked);
    }

    @SuppressLint("MissingPermission")
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_call:
                Authorizer.with(this)
                        .requestRuntime(Manifest.permission.CALL_PHONE)
                        .listener(new OnContinueListener() {
                            @Override
                            public void onContinue(final Continue aContinue) {
                                new AlertDialog.Builder(PermissionActivity.this)
                                        .setMessage("我需要拨号权限?")
                                        .setNegativeButton("好的", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onContinue();
                                            }
                                        })
                                        .setPositiveButton("不给", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onCancel();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .listener(new OnRuntimePermissionListener() {
                            @Override
                            public void onRuntimePermission(List<String> allow, List<String> reject, List<String> prohibit) {
                                if (allow.contains(Manifest.permission.CALL_PHONE)) {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:10010"));
                                    startActivity(intent);
                                } else {
                                    TT.show("没有拨号权限");
                                }
                            }
                        })
                        .request();
                break;
            case R.id.btn_camera:
                Authorizer.with(this)
                        .requestRuntime(Manifest.permission.CAMERA)
                        .listener(new OnContinueListener() {
                            @Override
                            public void onContinue(final Continue aContinue) {
                                new AlertDialog.Builder(PermissionActivity.this)
                                        .setMessage("我需要拍照权限?")
                                        .setNegativeButton("好的", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onContinue();
                                            }
                                        })
                                        .setPositiveButton("不给", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onCancel();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .listener(new OnRuntimePermissionListener() {
                            @Override
                            public void onRuntimePermission(List<String> allow, List<String> reject, List<String> prohibit) {
                                if (allow.contains(Manifest.permission.CAMERA)) {
                                    Multimedia.with(PermissionActivity.this)
                                            .take()
                                            .listener(new OnTakeListener() {
                                                @Override
                                                public void onSucceed(Uri uri) {
                                                    GlideApp.with(mViewBinding.ivPreview)
                                                            .asBitmap()
                                                            .load(uri)
                                                            .into(mViewBinding.ivPreview);
                                                }

                                                @Override
                                                public void onFailure(MultimediaException e) {

                                                }
                                            })
                                            .start();
                                } else {
                                    TT.show("没有拍照权限");
                                }
                            }
                        })
                        .request();
                break;
            case R.id.btn_call_camera:
                Authorizer.with(this)
                        .requestRuntime(Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA)
                        .listener(new OnContinueListener() {
                            @Override
                            public void onContinue(final Continue aContinue) {
                                new AlertDialog.Builder(PermissionActivity.this)
                                        .setMessage("我需要拨打电话和拍照权限?")
                                        .setNegativeButton("好的", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onContinue();
                                            }
                                        })
                                        .setPositiveButton("不给", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onCancel();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .listener(new OnRuntimePermissionListener() {
                            @Override
                            public void onRuntimePermission(List<String> allow, List<String> reject, List<String> prohibit) {
                                if (allow.contains(Manifest.permission.CALL_PHONE)) {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:10010"));
                                    startActivity(intent);
                                } else {
                                    TT.show("没有拨号权限");
                                }

                                if (allow.contains(Manifest.permission.CAMERA)) {
                                    Multimedia.with(PermissionActivity.this)
                                            .take()
                                            .listener(new OnTakeListener() {
                                                @Override
                                                public void onSucceed(Uri uri) {
                                                    GlideApp.with(mViewBinding.ivPreview)
                                                            .asBitmap()
                                                            .load(uri)
                                                            .into(mViewBinding.ivPreview);
                                                }

                                                @Override
                                                public void onFailure(MultimediaException e) {

                                                }
                                            })
                                            .start();
                                } else {
                                    TT.show("没有拍照权限");
                                }
                            }
                        })
                        .request();
                break;
            case R.id.btn_floats:
                Authorizer.with(this)
                        .requestFloat()
                        .listener(new OnContinueListener() {
                            @Override
                            public void onContinue(final Continue aContinue) {
                                new AlertDialog.Builder(PermissionActivity.this)
                                        .setMessage("我需要悬浮窗权限?")
                                        .setNegativeButton("好的", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onContinue();
                                            }
                                        })
                                        .setPositiveButton("不给", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onCancel();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .listener(new OnFloatPermissionListener() {
                            @Override
                            public void onSucceed() {
                                TT.show("成功获取悬浮窗权限");
                                new FloatWindow(getApplicationContext()).showFloatWindow();
                            }

                            @Override
                            public void onFailure() {
                                TT.show("没有悬浮窗权限");
                            }
                        })
                        .request();
                break;
            case R.id.btn_install:
                Authorizer.with(this)
                        .requestInstall()
                        .listener(new OnContinueListener() {
                            @Override
                            public void onContinue(final Continue aContinue) {
                                new AlertDialog.Builder(PermissionActivity.this)
                                        .setMessage("我需要安装应用权限?")
                                        .setNegativeButton("好的", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onContinue();
                                            }
                                        })
                                        .setPositiveButton("不给", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                aContinue.onCancel();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .listener(new OnInstallPermissionListener() {
                            @Override
                            public void onSucceed() {
                                boolean install = true;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    install = getPackageManager().canRequestPackageInstalls();
                                }
                                TT.show("成功获取安装应用权限,检测结果:" + install);
                            }

                            @Override
                            public void onFailure() {
                                TT.show("没有安装应用权限");
                            }
                        })
                        .request();
                break;
        }
    }
}
