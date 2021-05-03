package com.liux.android.example.qrcode;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.R;
import com.liux.android.qrcode.QRCode;
import com.liux.android.qrcode.QRCodeScanningCallback;
import com.liux.android.qrcode.QRCodeScanningFragment;
import com.liux.android.tool.TT;

public class QRCodeCustomizeScanningActivity extends AppCompatActivity {

    private QRCodeScanningFragment mQRCodeScanningFragment;
    private final QRCodeScanningCallback mQRCodeScanningCallback = new QRCodeScanningCallback() {

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
            TT.show("扫描到二维码:\n" + qrCode.getValue());
            mQRCodeScanningFragment.startScanning();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrcode_customize_scanning);

        // 下面代码处理了Activity旋转重建时重复创建Fragment的问题
        FragmentManager fragmentManager = getFragmentManager();
        mQRCodeScanningFragment = (QRCodeScanningFragment) fragmentManager.findFragmentById(R.id.fm_qrcode_scan);
        mQRCodeScanningFragment.setCallback(mQRCodeScanningCallback);
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
