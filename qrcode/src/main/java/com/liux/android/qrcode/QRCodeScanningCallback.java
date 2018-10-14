package com.liux.android.qrcode;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;

public interface QRCodeScanningCallback {
    int ERROR_PERMISSION = 0;
    int ERROR_CANNOT_OPEN = 1;

    /**
     * 创建解析器, 每次 reset 之后在重新开始解码之前调用
     * @return
     */
    MultiFormatReader onCreateReader();

    /**
     * 启动 Fragment 扫描出现错误而终止
     * @param error
     */
    void onError(int error);

    /**
     * 开始扫描
     */
    void onStartScan();

    /**
     * 停止扫描
     */
    void onStopScan();

    /**
     * 扫描结果
     * @param result
     */
    void onResult(Result result);

    /**
     * 检测到画面暗
     */
    void onDarkness();
}
