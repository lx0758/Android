package com.liux.android.qrcode.camrea;

import android.view.MotionEvent;
import android.view.SurfaceView;

public interface CameraManager {

    boolean isOpen();

    void open() throws Exception;

    void close();

    void bind(SurfaceView surfaceView) throws Exception;

    void startPreview();

    void stopPreview();

    void callManualFocus(MotionEvent event);

    void setPreviewCallback(PreviewCallback callback);
}
