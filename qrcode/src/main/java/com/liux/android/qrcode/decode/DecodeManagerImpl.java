package com.liux.android.qrcode.decode;

import com.liux.android.qrcode.camrea.CameraManager;
import com.liux.android.qrcode.camrea.PreviewCallback;
import com.liux.android.qrcode.camrea.PreviewFrame;

public class DecodeManagerImpl implements DecodeManager, PreviewCallback {

    private CameraManager mCameraManager;
    private DecodeHandler mDecodeHandler;

    @Override
    public void bind(CameraManager cameraManager, DecodeCallback decodeCallback) {
        if (decodeCallback == null) throw new NullPointerException("callback can not be empty");
        mCameraManager = cameraManager;
        mDecodeHandler = DecodeHandler.create(decodeCallback);
    }

    @Override
    public void reset() {
        mDecodeHandler.reset();
    }

    @Override
    public void startDecode() {
        if (mCameraManager != null) {
            mCameraManager.setPreviewCallback(this);
            mCameraManager.startPreview();
        }
    }

    @Override
    public void stopDecode() {
        if (mCameraManager != null) mCameraManager.stopPreview();
    }

    @Override
    public void destroy() {
        mDecodeHandler.destroy();
    }

    @Override
    public void onPreviewFrame(PreviewFrame previewFrame) {
        mDecodeHandler.resolve(previewFrame);
    }
}
