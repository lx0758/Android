package com.liux.android.example.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.liux.android.boxing.Cameraer;
import com.liux.android.example.R;
import com.liux.android.glide.GlideApp;
import com.liux.android.permission.Continue;
import com.liux.android.permission.OnContinueListener;
import com.liux.android.permission.floats.OnFloatPermissionListener;
import com.liux.android.permission.runtime.OnRuntimePermissionListener;
import com.liux.android.permission.PermissionTool;
import com.liux.android.util.UriUtil;
import com.liux.android.tool.TT;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/11/28.
 */

public class PermissionActivity extends AppCompatActivity {

    @BindView(R.id.iv_preview)
    ImageView ivPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_permission);
        ButterKnife.bind(this);
    }

    @SuppressLint("MissingPermission")
    @OnClick({R.id.btn_call, R.id.btn_camera, R.id.btn_call_camera, R.id.btn_floats})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_call:
                PermissionTool.with(this)
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
                                    TT.makeText(PermissionActivity.this, "没有拨号权限", TT.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .request();
                break;
            case R.id.btn_camera:
                PermissionTool.with(this)
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
                                    Cameraer.setAuthority(UriUtil.getAuthority(PermissionActivity.this));
                                    Cameraer.start(PermissionActivity.this, new Cameraer.OnCameraListener() {
                                        @Override
                                        public void onSucceed(File file) {
                                            GlideApp.with(ivPreview)
                                                    .asBitmap()
                                                    .load(file)
                                                    .into(ivPreview);
                                        }

                                        @Override
                                        public void onFailure(int type) {

                                        }
                                    });
                                } else {
                                    TT.makeText(PermissionActivity.this, "没有拍照权限", TT.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .request();
                break;
            case R.id.btn_call_camera:
                PermissionTool.with(this)
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
                                    TT.makeText(PermissionActivity.this, "没有拨号权限", TT.LENGTH_SHORT).show();
                                }

                                if (allow.contains(Manifest.permission.CAMERA)) {
                                    Cameraer.setAuthority(UriUtil.getAuthority(PermissionActivity.this));
                                    Cameraer.start(PermissionActivity.this, new Cameraer.OnCameraListener() {
                                        @Override
                                        public void onSucceed(File file) {
                                            GlideApp.with(ivPreview)
                                                    .asBitmap()
                                                    .load(file)
                                                    .into(ivPreview);
                                        }

                                        @Override
                                        public void onFailure(int type) {

                                        }
                                    });
                                } else {
                                    TT.makeText(PermissionActivity.this, "没有拍照权限", TT.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .request();
                break;
            case R.id.btn_floats:
                PermissionTool.with(this)
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
                                TT.makeText(PermissionActivity.this, "成功获取悬浮窗权限", TT.LENGTH_SHORT).show();
                                new FloatWindow(getApplicationContext()).showFloatWindow();
                            }

                            @Override
                            public void onFailure() {
                                TT.makeText(PermissionActivity.this, "没有悬浮窗权限", TT.LENGTH_SHORT).show();
                            }
                        })
                        .request();
                break;
        }
    }
}
