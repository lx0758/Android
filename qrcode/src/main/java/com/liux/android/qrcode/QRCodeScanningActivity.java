package com.liux.android.qrcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.liux.android.qrcode.camrea.LightManager;

public class QRCodeScanningActivity extends Activity implements QRCodeScanningCallback {
    private static final String TAG = "QRCodeScanningActivity";
    private static final String RESULT_QRCODE_KEY = "RESULT_QRCODE_KEY";

    /**
     * 从返回的 {@link Intent} 中获取二维码值
     * @param intent
     * @return
     */
    public static String resolveQRCode(Intent intent) {
        if (intent == null) return null;
        return intent.getStringExtra(RESULT_QRCODE_KEY);
    }

    private boolean mManualCloseLight = false;
    private ImageView mSCanLine, mLightSwitch;
    private QRCodeScanningFragment mQRCodeScanningFragment;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switchLight();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_qrcode_scaning_portrait);
        } else {
            setContentView(R.layout.activity_qrcode_scaning_landscape);
        }


        mSCanLine = findViewById(R.id.iv_scan_line);
        mLightSwitch = findViewById(R.id.iv_light_switch);

        // 下面代码处理了Activity旋转重建时重复创建Fragment的问题
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        if (fragment instanceof QRCodeScanningFragment) {
            mQRCodeScanningFragment = (QRCodeScanningFragment) fragment;
            mQRCodeScanningFragment.setCallback(this);
        } else {
            mQRCodeScanningFragment = new QRCodeScanningFragment();
            mQRCodeScanningFragment.setCallback(this);
            fragmentManager.beginTransaction()
                    .add(R.id.fl_container, mQRCodeScanningFragment, TAG)
                    .commit();
        }
    }

    @Override
    public MultiFormatReader onCreateReader() {
        return QRCodeDecoder.getMultiFormatReader();
    }

    @Override
    public void onError(int error) {
        switch (error) {
            case QRCodeScanningCallback.ERROR_PERMISSION:
                showError("没有授权访问摄像头权限");
                break;
            case QRCodeScanningCallback.ERROR_CANNOT_OPEN:
                showError("不能成功打开摄像头");
                break;
        }
    }

    @Override
    public void onStartScan() {
        mSCanLine.clearAnimation();
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.90f
        );
        animation.setDuration(3500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        mSCanLine.startAnimation(animation);
        mSCanLine.setVisibility(View.VISIBLE);

        if (mQRCodeScanningFragment.getLightManager().canOpenLight()) {
            mLightSwitch.setVisibility(View.VISIBLE);
        }

        mLightSwitch.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onStopScan() {
        mSCanLine.clearAnimation();
        mSCanLine.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResult(Result result) {
        // if you want continue scan and ignore results
        // mQRCodeScanningFragment.reset();
        Intent intent = new Intent();
        intent.putExtra(RESULT_QRCODE_KEY, result.getText());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDarkness() {
        if (mManualCloseLight) return;
        LightManager lightManager = mQRCodeScanningFragment.getLightManager();
        if (!lightManager.canOpenLight() || lightManager.isOpenLight()) return;
        openLight(lightManager);
    }

    private void switchLight() {
        LightManager lightManager = mQRCodeScanningFragment.getLightManager();
        if (!lightManager.canOpenLight()) return;

        if (!lightManager.isOpenLight()) {
            openLight(lightManager);
        } else {
            closeLight(lightManager);
        }
    }

    private void openLight(LightManager lightManager) {
        lightManager.openLight();
        mLightSwitch.setImageResource(R.drawable.qrcode_scan_light_close);
    }

    private void closeLight(LightManager lightManager) {
        mManualCloseLight = true;
        lightManager.closeLight();
        mLightSwitch.setImageResource(R.drawable.qrcode_scan_light_open);
    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }
}
