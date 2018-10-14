package com.liux.android.qrcode.camrea;

import android.hardware.Camera;

public class OpenCamera {

    private final int index;
    private final Camera camera;
    private final Camera.CameraInfo cameraInfo;

    public OpenCamera(int index, Camera camera, Camera.CameraInfo cameraInfo) {
        this.index = index;
        this.camera = camera;
        this.cameraInfo = cameraInfo;
    }

    public Camera getCamera() {
        return camera;
    }

    public Camera.CameraInfo getCameraInfo() {
        return cameraInfo;
    }

    public int getFacing() {
        return cameraInfo.facing;
    }

    public int getOrientation() {
        return cameraInfo.orientation;
    }

    @Override
    public String toString() {
        return "Camera #" + index + " : " + getFacing() + ',' + getOrientation();
    }
}
