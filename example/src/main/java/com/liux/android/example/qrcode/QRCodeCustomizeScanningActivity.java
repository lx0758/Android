package com.liux.android.example.qrcode;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.liux.android.example.R;
import com.liux.android.qrcode.QRCodeDecoder;
import com.liux.android.qrcode.QRCodeScanningCallback;
import com.liux.android.qrcode.QRCodeScanningFragment;
import com.liux.android.tool.TT;

public class QRCodeCustomizeScanningActivity extends AppCompatActivity {

    private QRCodeScanningFragment mQRCodeScanningFragment;
    private QRCodeScanningCallback mQRCodeScanningCallback = new QRCodeScanningCallback() {
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

        }

        @Override
        public void onStopScan() {

        }

        @Override
        public void onResult(Result result) {
            TT.show(QRCodeCustomizeScanningActivity.this, "扫描到二维码:\n" + result.getText(), TT.LENGTH_SHORT);
            mQRCodeScanningFragment.reset();
        }

        @Override
        public void onDarkness() {
            mQRCodeScanningFragment.getLightManager().openLight();
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
