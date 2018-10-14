package com.liux.android.qrcode.camrea;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

public class OpenCameraConfig {

    // 摄像头安装角度(面对屏幕竖屏状态为0度,向左旋转为正)
    private int cameraAngle;
    // 显示屏当前角度(面对屏幕竖屏状态为0度,向左旋转为正)
    private int displayAngle;
    // 摄像头相对显示屏需要修正的角度(面对屏幕竖屏状态为0度,向左旋转为正)
    private int cameraToDisplayAngle;
    // 屏幕的分辨率
    private Point screenSize;
    // 预览 View 的分辨率
    private Point previewSize;
    // 从摄像头选择的分辨率
    private Point cameraSize;

    public void initParameters(OpenCamera camera, View view) throws Exception {
        WindowManager manager = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        cameraAngle = camera.getOrientation();
        int displayRotation = display.getRotation();
        switch (displayRotation) {
            case Surface.ROTATION_0:
                displayAngle = 0;
                break;
            case Surface.ROTATION_90:
                displayAngle = 90;
                break;
            case Surface.ROTATION_180:
                displayAngle = 180;
                break;
            case Surface.ROTATION_270:
                displayAngle = 270;
                break;
            default:
                // Have seen this return incorrect values like -90
                if (displayRotation % 90 == 0) {
                    displayAngle = (360 + displayRotation) % 360;
                } else {
                    throw new IllegalArgumentException("Bad rotation: " + displayRotation);
                }
                break;
        }
        // Still not 100% sure about this. But acts like we need to flip this:
        if (camera.getFacing() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraAngle = (360 - cameraAngle) % 360;
        }
        cameraToDisplayAngle = (360 + cameraAngle - displayAngle) % 360;
        if (camera.getFacing() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraToDisplayAngle = (360 - cameraToDisplayAngle) % 360;
        }

        Camera.Parameters parameters = camera.getCamera().getParameters();

        Point point = new Point();
        display.getSize(point);
        screenSize = point;
        previewSize = new Point(view.getMeasuredWidth(), view.getMeasuredHeight());
        cameraSize = CameraUtil.findBestPreviewSizeValue(parameters, cameraAngle, previewSize, displayRotation);
    }

    public void applyParameters(OpenCamera openCamera) {
        Camera camera = openCamera.getCamera();
        Camera.Parameters parameters = camera.getParameters();

        parameters.setPreviewSize(cameraSize.x, cameraSize.y);

        camera.setParameters(parameters);
        camera.setDisplayOrientation(cameraToDisplayAngle);

        Camera.Parameters afterParameters = camera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize != null && (cameraSize.x != afterSize.width || cameraSize.y != afterSize.height)) {
            cameraSize.x = afterSize.width;
            cameraSize.y = afterSize.height;
        }
    }

    public int getCameraAngle() {
        return cameraAngle;
    }

    public int getDisplayAngle() {
        return displayAngle;
    }

    public int getCameraToDisplayAngle() {
        return cameraToDisplayAngle;
    }

    public Point getScreenSize() {
        return screenSize;
    }

    public Point getPreviewSize() {
        return previewSize;
    }

    public Point getCameraSize() {
        return cameraSize;
    }
}
