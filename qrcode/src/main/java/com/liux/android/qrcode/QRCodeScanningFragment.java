package com.liux.android.qrcode;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.liux.android.qrcode.camrea.CameraManager;
import com.liux.android.qrcode.camrea.CameraManagerImpl;
import com.liux.android.qrcode.camrea.LightManager;
import com.liux.android.qrcode.decode.DecodeCallback;
import com.liux.android.qrcode.decode.DecodeManager;
import com.liux.android.qrcode.decode.DecodeManagerImpl;

/**
 * 注意处理 Activity 中多次创建 Fragment 的问题
 * 一种方法是在创建时调用 {@link FragmentManager#findFragmentById(int)} 查找
 * 另一种是避免 Activity 的销毁重建事件,可以再清单文件中加上以下内容
 * android:configChanges="orientation|screenSize|keyboardHidden|keyboard|screenLayout"
 */
public class QRCodeScanningFragment extends Fragment implements DecodeCallback {

    private int mRequestCode;
    private boolean hasSurfaceHolder = false;

    private SurfaceView mSurfaceView;

    private LightManager mLightManager;
    private CameraManager mCameraManager;
    private DecodeManager mDecodeManager;
    private QRCodeScanningCallback mQRCodeScanningCallback;

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            hasSurfaceHolder = true;
            checkAndStartPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            hasSurfaceHolder = false;
            stopScanning();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraManager = new CameraManagerImpl();
        mDecodeManager = new DecodeManagerImpl();
        mLightManager = (LightManager) mCameraManager;

        mDecodeManager.bind(mCameraManager, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = container != null ? container.getContext() : getActivity();
        mSurfaceView = new SurfaceView(context);
        mSurfaceView.setBackgroundColor(0x00000000);
        mSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSurfaceView.getHolder().addCallback(mCallback);
        mSurfaceView.getHolder().setKeepScreenOn(true);
        return mSurfaceView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.performClick()) return true;
                mCameraManager.callManualFocus(event);
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDecodeManager.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != mRequestCode) return;
        for (String p : permissions) {
            if (!Manifest.permission.CAMERA.equals(p)) continue;

            if (PermissionChecker.checkCallingOrSelfPermission(getActivity(), Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
                startScanning();
                return;
            }
        }
        callError(QRCodeScanningCallback.ERROR_PERMISSION);
    }

    @Override
    public MultiFormatReader onCreateReader() {
        return mQRCodeScanningCallback.onCreateReader();
    }

    @Override
    public void onDecodeResult(final Result result) {
        if (mQRCodeScanningCallback != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mQRCodeScanningCallback.onResult(result);
                }
            });
        }
    }

    @Override
    public void onLightness(float[] lightness) {
        float lightnessCount = 0;
        for (float light : lightness) {
            lightnessCount += light;
        }
        boolean darkness = lightnessCount / lightness.length < 0.15F;

        if (!darkness) return;
        if (!mLightManager.canOpenLight() || mLightManager.isOpenLight()) return;

        if (mQRCodeScanningCallback != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mQRCodeScanningCallback.onDarkness();
                }
            });
        }
    }

    public LightManager getLightManager() {
        return mLightManager;
    }

    public void setCallback(QRCodeScanningCallback qrCodeScanningCallback) {
        mQRCodeScanningCallback = qrCodeScanningCallback;
    }

    public QRCodeScanningCallback getCallback() {
        return mQRCodeScanningCallback;
    }

    /**
     * 扫描出结果之后继续扫描
     */
    public void reset() {
        mDecodeManager.reset();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndStartPreview() {
        if (PermissionChecker.checkCallingOrSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mRequestCode = getRequestCode();
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    mRequestCode
            );
            return;
        }
        startScanning();
    }

    private void startScanning() {
        if (!hasSurfaceHolder) return;

        try {
            mCameraManager.open();
            mCameraManager.bind(mSurfaceView);
        } catch (Exception e) {
            callError(QRCodeScanningCallback.ERROR_CANNOT_OPEN);
            return;
        }

        mDecodeManager.startDecode();
        if (mQRCodeScanningCallback != null) mQRCodeScanningCallback.onStartScan();
    }

    private void stopScanning() {
        if (mCameraManager.isOpen()) {
            mDecodeManager.stopDecode();
            mCameraManager.close();
            if (mQRCodeScanningCallback != null) mQRCodeScanningCallback.onStopScan();
        }
    }

    private void callError(int error) {
        if (mQRCodeScanningCallback != null) {
            mQRCodeScanningCallback.onError(error);
        }
    }

    /**
     * 获取一个随机的请求码
     * @return
     */
    private int getRequestCode() {
        return (int) (System.currentTimeMillis() & 0xFFFF);
    }
}
