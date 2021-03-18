package com.liux.android.qrcode.camrea;

import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.List;

public class CameraManagerImpl implements CameraManager, LightManager {
    private static final String TAG = CameraManagerImpl.class.getName();

    private OpenCamera mOpenCamrea;
    private FocusManager mFocusManager;
    private PreviewCallback mPreviewCallback;
    private OpenCameraConfig mOpenCameraConfig;

    private Camera.PreviewCallback mCameraPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (mPreviewCallback != null) {
                int width = mOpenCameraConfig.getCameraSize().x;
                int height = mOpenCameraConfig.getCameraSize().y;
                mPreviewCallback.onPreviewFrame(
                        new PreviewFrame(data, width, height)
                );
            }
        }
    };

    public CameraManagerImpl() {
        mOpenCameraConfig = new OpenCameraConfig();
    }

    @Override
    public boolean isOpen() {
        return mOpenCamrea != null;
    }

    @Override
    public synchronized void open() throws Exception {
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) throw new NullPointerException("Can't find cameras!");

        // find facing is CAMERA_FACING_BACK camrea
        int cameraId = 0;
        while (cameraId < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                break;
            }
            cameraId++;
        }
        // no find camera facing CAMERA_FACING_BACK, Use camera #0
        if (cameraId == numCameras) cameraId = 0;

        // get camera info
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        Camera camera = Camera.open(cameraId);
        if (camera == null) throw new NullPointerException("Can't open cameras!");

        mOpenCamrea = new OpenCamera(
                cameraId,
                camera,
                cameraInfo
        );
    }

    @Override
    public synchronized void close() {
        if (isOpen()) {
            closeLight();
            mOpenCamrea.getCamera().release();
        }
        mOpenCamrea = null;
    }

    @Override
    public synchronized void bind(SurfaceView surfaceView) throws Exception {
        if (!isOpen()) return;
        mOpenCameraConfig.initParameters(mOpenCamrea, surfaceView);
        mOpenCameraConfig.applyParameters(mOpenCamrea);
        mOpenCamrea.getCamera().setPreviewDisplay(surfaceView.getHolder());
    }

    @Override
    public void startPreview() {
        if (!isOpen()) return;

        mOpenCamrea.getCamera().startPreview();
        mOpenCamrea.getCamera().setPreviewCallback(mCameraPreviewCallback);

        mFocusManager = new FocusManager(mOpenCamrea, mOpenCameraConfig);
        mFocusManager.start();
    }

    @Override
    public void stopPreview() {
        if (!isOpen()) return;

        mOpenCamrea.getCamera().setPreviewCallback(null);
        mOpenCamrea.getCamera().stopPreview();
        mFocusManager.stop();
    }

    @Override
    public void callManualFocus(MotionEvent event) {
        if (!isOpen()) return;
        if (mFocusManager != null) mFocusManager.callManualFocus(event);
    }

    @Override
    public void setPreviewCallback(PreviewCallback callback) {
        mPreviewCallback = callback;
    }

    @Override
    public boolean canOpenLight() {
        if (!isOpen()) return false;
        Camera.Parameters parameters = mOpenCamrea.getCamera().getParameters();
        List<String> modes = parameters.getSupportedFlashModes();
        if (modes == null) return false;
        return modes.contains(Camera.Parameters.FLASH_MODE_TORCH);
    }

    @Override
    public boolean isOpenLight() {
        if (!isOpen() || !canOpenLight()) return false;
        Camera.Parameters parameters = mOpenCamrea.getCamera().getParameters();
        return Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode());
    }

    @Override
    public void openLight() {
        if (isOpenLight()) return;
        setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
    }

    @Override
    public void closeLight() {
        if (!isOpenLight()) return;
        setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
    }

    private void setFlashMode(String mode) {
        if (!isOpen() || !canOpenLight()) return;
        try {
            Camera.Parameters parameters = mOpenCamrea.getCamera().getParameters();
            parameters.setFlashMode(mode);
            mOpenCamrea.getCamera().setParameters(parameters);
        } catch (Exception ignore) {}
    }
}
