package com.liux.android.qrcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

public class QRCodeScanningActivity extends Activity implements QRCodeScanningCallback {
    private static final String TAG = "QRCodeScanningActivity";
    private static final String RESULT_QRCODE = "RESULT_QRCODE";

    /**
     * 从返回的 {@link Intent} 中获取二维码值
     * @param intent
     * @return
     */
    public static String resolveQRCode(Intent intent) {
        if (intent == null) return null;
        return intent.getStringExtra(RESULT_QRCODE);
    }

    private QRCodeScanningFragment mQRCodeScanningFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrcode_scaning);

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
    public boolean onChecking(QRCode qrCode) {
        return true;
    }

    @Override
    public void onResult(QRCode qrCode) {
        // if you want continue scan and ignore results
        // mQRCodeScanningFragment.reset();
        Intent intent = new Intent();
        intent.putExtra(RESULT_QRCODE, qrCode.getValue());
        setResult(RESULT_OK, intent);
        finish();
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
