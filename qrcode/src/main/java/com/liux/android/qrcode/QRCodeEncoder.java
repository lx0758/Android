package com.liux.android.qrcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;

/**
 * Created by Liux
 * Email:lx0758@qq.com
 */

public class QRCodeEncoder {

    /**
     * 将文本编码为二维码
     * @param content
     * @param outWidth
     * @param outHeight
     * @param logo
     * @return
     * @throws WriterException
     */
    public static Bitmap encode(String content, int outWidth, int outHeight, Bitmap logo) {
        // 获取适合的尺寸
        int size = Math.min(outWidth, outHeight);
        outWidth = outHeight = size;

        // 生成二维码,以最小尺寸生成矩阵
        BitMatrix bitMatrix;
        try {
            HashMap<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 1,  1, hints);
        } catch (Exception e) {
            return null;
        }
        int qrWidth = bitMatrix.getWidth();
        int qrHeight = bitMatrix.getHeight();

        // 缩放 Logo 至矩阵的中间
        Rect rect = null;
        if (logo == null || logo.getWidth() == 0 || logo.getHeight() == 0) {
            logo = null;
        } else {
            float ratioW = (qrWidth / 2) / (float)logo.getWidth();
            float ratioH = (qrHeight / 2) / (float)logo.getHeight();
            float ratio = Math.min(ratioW, ratioH);
            int targetWidth = (int) (logo.getWidth() * ratio);
            int targetHeight = (int) (logo.getHeight() * ratio);
            logo = Bitmap.createScaledBitmap(logo, targetWidth, targetHeight, false);
            rect = new Rect(qrWidth / 4, qrHeight / 4, qrWidth / 4 * 3, qrHeight / 4 * 3);
        }

        // 画矩阵到 Bitmap
        Bitmap qrBitmap = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.RGB_565);
        for (int i = 0; i < qrWidth; i++) {
            for (int j = 0; j < qrHeight; j++) {
                if (logo != null && rect.contains(i, j)) {
                    int logoX = i - rect.left;
                    int logoY = j - rect.top;
                    if (logo.getPixel(logoX, logoY) != Color.TRANSPARENT && bitMatrix.get(i, j)) {
                        qrBitmap.setPixel(i, j, logo.getPixel(logoX, logoY));
                    } else {
                        qrBitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                } else {
                    qrBitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
        }

        // 缩放到目标大小
        return Bitmap.createScaledBitmap(qrBitmap, outWidth, outHeight, false);
    }
}
