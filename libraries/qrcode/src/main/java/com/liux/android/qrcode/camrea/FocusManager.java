package com.liux.android.qrcode.camrea;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

public class FocusManager {
    private static final int AUTO_FOCUS_INTERVAL_MSG = 1;
    private static final long AUTO_FOCUS_INTERVAL_MS = 1200L;

    private Camera mCamera;
    private String mCurrentFocusMode;
    private OpenCameraConfig mOpenCameraConfig;

    private boolean mEnableFocus = false, mFocusing = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mFocusing) {
                mFocusing = true;
                if (msg.obj == null) {
                    callAutoFocus();
                } else {
                    callManualFocus(msg.obj);
                }
            }
        }

        private void callAutoFocus() {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(mCurrentFocusMode);
            try {
                mCamera.cancelAutoFocus();
                mCamera.setParameters(parameters);
            } catch (Exception ignore) {}
            mCamera.autoFocus(mAutoFocusCallback);
        }

        /**
         * 手动点击对焦
         * https://blog.csdn.net/candycat1992/article/details/21617741
         * @param obj
         */
        private void callManualFocus(Object obj) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(mCurrentFocusMode);

            if (!(obj instanceof MotionEvent) || parameters.getMaxNumFocusAreas() <= 0) {
                callAutoFocus();
                return;
            }
            MotionEvent event = (MotionEvent) obj;
            Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(), 1f);
            Rect meteringRect = calculateTapArea(event.getRawX(), event.getRawY(), 1.5f);

            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(focusRect, 1000));
                parameters.setFocusAreas(focusAreas);
            }
            if (parameters.getMaxNumMeteringAreas() > 0) {
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(meteringRect, 1000));
                parameters.setMeteringAreas(meteringAreas);
            }

            try {
                mCamera.cancelAutoFocus();
                mCamera.setParameters(parameters);
            } catch (Exception ignore) {}
            mCamera.autoFocus(mAutoFocusCallback);
        }

        /**
         * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
         */
        private Rect calculateTapArea(float x, float y, float coefficient) {
            float focusAreaSize = 300;
            Point screenSize = mOpenCameraConfig.getScreenSize();
            int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
            int centerX = (int) (x / screenSize.x * 2000 - 1000);
            int centerY = (int) (y / screenSize.y * 2000 - 1000);
            int left = clamp(centerX - areaSize / 2, -1000, 1000);
            int right = clamp(left + areaSize, -1000, 1000);
            int top = clamp(centerY - areaSize / 2, -1000, 1000);
            int bottom = clamp(top + areaSize, -1000, 1000);
            return new Rect(left, top, right, bottom);
        }

        private int clamp(int x, int min, int max) {
            if (x > max) return max;
            if (x < min) return min;
            return x;
        }
    };

    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            mFocusing = false;
            callAutoFocus(AUTO_FOCUS_INTERVAL_MS, null);
        }
    };

    public FocusManager(OpenCamera openCamrea, OpenCameraConfig openCameraConfig) {
        mCamera = openCamrea.getCamera();
        mOpenCameraConfig = openCameraConfig;

        Camera.Parameters parameters = mCamera.getParameters();
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        String currentFocusMode = CameraUtil.findSettableValue(
                "focus-mode",
                supportedFocusModes,
                Camera.Parameters.FOCUS_MODE_AUTO,
                Camera.Parameters.FOCUS_MODE_MACRO
        );
        if (currentFocusMode != null && !currentFocusMode.isEmpty()) {
            mCurrentFocusMode = currentFocusMode;
            mEnableFocus = true;
        }
    }

    public void start() {
        callAutoFocus(0, null);
    }

    public void stop() {
        if (!mEnableFocus) return;

        mCamera.cancelAutoFocus();
        mHandler.removeMessages(AUTO_FOCUS_INTERVAL_MSG);
    }

    public synchronized void callManualFocus(MotionEvent event) {
        callAutoFocus(0, event);
    }

    private void callAutoFocus(long delay, MotionEvent event) {
        if (!mEnableFocus) return;

        mHandler.removeMessages(AUTO_FOCUS_INTERVAL_MSG);
        if (event == null) {
            mHandler.sendEmptyMessageDelayed(AUTO_FOCUS_INTERVAL_MSG, delay);
        } else {
            Message message = mHandler.obtainMessage();
            message.what = AUTO_FOCUS_INTERVAL_MSG;
            message.obj = event;
            mHandler.sendMessageDelayed(message, delay);
        }
    }
}
