package com.liux.android.qrcode.decode;

import com.liux.android.qrcode.camrea.CameraManager;

public interface DecodeManager {

    void bind(CameraManager cameraManager, DecodeCallback decodeCallback);

    void startDecode();

    void stopDecode();

    void destroy();
}
