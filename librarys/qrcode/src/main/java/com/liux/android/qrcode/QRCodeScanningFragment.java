package com.liux.android.qrcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;

import com.liux.android.qrcode.camrea.CameraManager;
import com.liux.android.qrcode.camrea.CameraManagerImpl;
import com.liux.android.qrcode.camrea.LightManager;
import com.liux.android.qrcode.decode.DecodeCallback;
import com.liux.android.qrcode.decode.DecodeManager;
import com.liux.android.qrcode.decode.DecodeManagerImpl;
import com.liux.android.qrcode.util.SizeUtil;
import com.liux.android.qrcode.view.ResultLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 注意处理 Activity 中多次创建 Fragment 的问题
 * 一种方法是在创建时调用 {@link FragmentManager#findFragmentById(int)} 查找
 * 另一种是避免 Activity 的销毁重建事件,可以再清单文件中加上以下内容
 * android:configChanges="orientation|screenSize|keyboardHidden|keyboard|screenLayout"
 */
public class QRCodeScanningFragment extends Fragment {

    private int mRequestCode;
    private boolean mHasSurfaceHolder = false;

    private SurfaceView mSurfaceView;
    private ResultLayout mResultLayout;
    private ImageView mLine, mBack, mLight;

    private LightManager mLightManager;
    private CameraManager mCameraManager;
    private DecodeManager mDecodeManager;
    private QRCodeScanningCallback mQRCodeScanningCallback;

    private final SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mHasSurfaceHolder = true;
            checkAndStartPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mHasSurfaceHolder = false;
            stopScanning();
        }
    };

    private final DecodeCallback mDecodeCallback = new DecodeCallback() {
        @Override
        public void onDecodeResult(List<QRCode> qrCodes) {
            if (mQRCodeScanningCallback == null) return;

            final List<QRCode> result = new ArrayList<>();
            for (QRCode qrCode : qrCodes) {
                boolean valid = mQRCodeScanningCallback.onChecking(qrCode);
                if (valid) {
                    result.add(qrCode);
                }
            }

            if (result.isEmpty()) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callResult(result);
                }
            });
        }

        @Override
        public void onLightness(final float lightness) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mLightManager.isOpenLight()) return;
                    if (lightness < 0.2) {
                        if (mLight.getVisibility() != View.VISIBLE) mLight.setVisibility(View.VISIBLE);
                    } else {
                        if (mLight.getVisibility() != View.INVISIBLE) mLight.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    };

    private final ResultLayout.OnResultListener mOnResultListener = new ResultLayout.OnResultListener() {
        @Override
        public void onResult(QRCode qrCode) {
            mQRCodeScanningCallback.onResult(qrCode);
        }

        @Override
        public void onCancel() {
            mResultLayout.clear();
            startScanning();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraManager = new CameraManagerImpl();
        mDecodeManager = new DecodeManagerImpl();
        mLightManager = (LightManager) mCameraManager;

        mDecodeManager.bind(mCameraManager, mDecodeCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = container != null ? container.getContext() : getActivity();

        int buttonSize = SizeUtil.getButtonSize(context);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.setBackgroundColor(0x00000000);
        mSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSurfaceView.getHolder().addCallback(mCallback);
        mSurfaceView.getHolder().setKeepScreenOn(true);
        frameLayout.addView(mSurfaceView);

        FrameLayout.LayoutParams lineLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtil.dp2px(context, 10));
        lineLayoutParams.leftMargin = SizeUtil.dp2px(context, 30);
        lineLayoutParams.rightMargin = SizeUtil.dp2px(context, 30);
        mLine = new ImageView(context);
        mLine.setImageResource(R.drawable.qrcode_scan_line);
        mLine.setScaleType(ImageView.ScaleType.FIT_XY);
        mLine.setLayoutParams(lineLayoutParams);
        mLine.setVisibility(View.INVISIBLE);
        frameLayout.addView(mLine);

        mResultLayout = new ResultLayout(context);
        mResultLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mResultLayout.setOnSelectListener(mOnResultListener);
        frameLayout.addView(mResultLayout);

        FrameLayout.LayoutParams backLayoutParams = new FrameLayout.LayoutParams(buttonSize, buttonSize);
        backLayoutParams.gravity = Gravity.TOP | Gravity.START;
        backLayoutParams.topMargin = SizeUtil.dp2px(context, 40);
        backLayoutParams.leftMargin = SizeUtil.dp2px(context, 25);
        mBack = new ImageView(context);
        mBack.setImageResource(R.drawable.qrcode_scan_close);
        mBack.setLayoutParams(backLayoutParams);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        frameLayout.addView(mBack);

        FrameLayout.LayoutParams lightLayoutParams = new FrameLayout.LayoutParams(buttonSize, buttonSize);
        lightLayoutParams.gravity = Gravity.TOP | Gravity.END;
        lightLayoutParams.topMargin = SizeUtil.dp2px(context, 40);
        lightLayoutParams.rightMargin = SizeUtil.dp2px(context, 25);
        mLight = new ImageView(context);
        mLight.setImageResource(R.drawable.qrcode_scan_camera_flash_on);
        mLight.setLayoutParams(lightLayoutParams);
        mLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLightManager.isOpenLight()) {
                    mLightManager.closeLight();
                    mLight.setImageResource(R.drawable.qrcode_scan_camera_flash_on);
                } else {
                    mLightManager.openLight();
                    mLight.setImageResource(R.drawable.qrcode_scan_camera_flash_off);
                }
            }
        });
        mLight.setVisibility(View.INVISIBLE);
        frameLayout.addView(mLight);

        return frameLayout;
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

    public void setCallback(QRCodeScanningCallback qrCodeScanningCallback) {
        mQRCodeScanningCallback = qrCodeScanningCallback;
    }

    public void startScanning() {
        if (!mHasSurfaceHolder) return;

        try {
            mCameraManager.open();
            mCameraManager.bind(mSurfaceView);
        } catch (Exception e) {
            callError(QRCodeScanningCallback.ERROR_CANNOT_OPEN);
            return;
        }

        mResultLayout.clear();
        mDecodeManager.startDecode();

        mLine.clearAnimation();
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.2f,
                Animation.RELATIVE_TO_PARENT,
                0.8f
        );
        animation.setDuration(2000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        mLine.startAnimation(animation);
        mLine.setVisibility(View.VISIBLE);
    }

    public void stopScanning() {
        if (mCameraManager.isOpen()) {
            mDecodeManager.stopDecode();
            mCameraManager.close();
        }
        mLine.clearAnimation();
        mLine.setVisibility(View.INVISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndStartPreview() {
        if (PermissionChecker.checkCallingOrSelfPermission(getActivity(), Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            mRequestCode = getRequestCode();
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    mRequestCode
            );
            return;
        }
        startScanning();
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

    private void callResult(List<QRCode> qrCodes) {
        stopScanning();
        playVibrator();
        if (qrCodes.size() == 1) {
            QRCode qrCode = qrCodes.get(0);
            mResultLayout.showResult(qrCode);
        } else {
            mResultLayout.selectResult(qrCodes);
        }
    }

    @SuppressLint("MissingPermission")
    private void playVibrator() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
}
