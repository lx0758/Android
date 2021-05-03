package com.liux.android.qrcode;

import java.util.List;

public interface QRCodeScanningCallback {
    int ERROR_PERMISSION = 0;
    int ERROR_CANNOT_OPEN = 1;

    /**
     * 启动 Fragment 扫描出现错误而终止
     * @param error
     */
    void onError(int error);

    /**
     * 检查是否是有效的扫描结果
     * @param qrCode
     * @return
     */
    boolean onChecking(QRCode qrCode);

    /**
     * 扫描结果
     * @param qrCode
     */
    void onResult(QRCode qrCode);
}
