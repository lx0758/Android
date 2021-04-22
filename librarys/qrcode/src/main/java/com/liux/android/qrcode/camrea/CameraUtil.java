package com.liux.android.qrcode.camrea;

import android.graphics.Point;
import android.hardware.Camera;
import android.view.Surface;

import java.util.Collection;
import java.util.List;

public class CameraUtil {

    public static Point findBestPreviewSizeValue(Camera.Parameters parameters, int cameraAngle, Point previewSize, int previewRotation) throws Exception {
        // 将两个分辨率变换为0度状态再进行比对
        boolean needRotate = false;
        if (cameraAngle == 90 || cameraAngle == 270) needRotate = true;
        if (previewRotation == Surface.ROTATION_90 || previewRotation == Surface.ROTATION_270) {
            previewSize = new Point(previewSize.y, previewSize.x);
        }

        // 取出所有支持分辨率
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (defaultSize == null) {
                throw new IllegalStateException("Parameters contained no preview size!");
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        // 预览视图横纵比
        double previewAspectRatio = previewSize.x / (double) previewSize.y;

        // 官方Demo中寻找不到和期望的尺寸一致的 Size 时采用最大分辨率的 Size
        // 这里改成横纵比最接近期望的那个 Size
        double fitAspectRatio = 0.3d;
        Camera.Size fitPreviewSize = null;
        for (Camera.Size size : rawSupportedSizes) {
            int width = size.width;
            int height = size.height;
            if (needRotate) {
                width = size.height;
                height = size.width;
            }

            // 分辨率必须大于 480 * 320
            int resolution = width * height;
            if (resolution < 480 * 320) continue;

            // 过滤掉分辨率横纵比相差特别大的 Size
            double aspectRatio = width / (double) height;
            double distortion = Math.abs(aspectRatio - previewAspectRatio);
            if (distortion > 0.3) continue;

            if (width == previewSize.x && height == previewSize.y) {
                return new Point(size.width, size.height);
            }

            if (distortion < fitAspectRatio) {
                fitAspectRatio = distortion;
                fitPreviewSize = size;
            }
        }

        if (fitPreviewSize != null) {
            return new Point(fitPreviewSize.width, fitPreviewSize.height);
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        if (defaultPreview == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        return new Point(defaultPreview.width, defaultPreview.height);
    }

    public static String findSettableValue(String name, Collection<String> supportedValues, String... desiredValues) {
        if (supportedValues != null) {
            for (String desiredValue : desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    return desiredValue;
                }
            }
        }
        return null;
    }
}
