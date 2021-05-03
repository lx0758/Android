package com.liux.android.qrcode;

import android.graphics.Rect;

import com.google.mlkit.vision.barcode.Barcode;
import com.liux.android.qrcode.camrea.PreviewFrame;

import java.util.ArrayList;
import java.util.List;

public class QRCode {

    private float xRatio;
    private float yRatio;
    private Barcode barcode;

    public static List<QRCode> from(PreviewFrame previewFrame, List<Barcode> barcodes) {
        List<QRCode> qrCodes = new ArrayList<>();
        if (barcodes == null) return qrCodes;

        boolean convert = previewFrame.getAngle() % 180 != 0;
        int xWidth = convert ? previewFrame.getHeight() : previewFrame.getWidth();
        int yHeiht = convert ? previewFrame.getWidth() : previewFrame.getHeight();

        for (Barcode barcode : barcodes) {
            Rect rect = barcode.getBoundingBox();
            float xRatio = (rect.left + rect.width() / 2.0F) / xWidth;
            float yRatio = (rect.top + rect.height() / 2.0F) / yHeiht;
            QRCode qrCode = new QRCode(xRatio, yRatio, barcode);
            qrCodes.add(qrCode);
        }

        return qrCodes;
    }

    public QRCode(float xRatio, float yRatio, Barcode barcode) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        this.barcode = barcode;
    }

    public float getXRatio() {
        return xRatio;
    }

    public float getYRatio() {
        return yRatio;
    }

    public String getValue() {
        return barcode.getRawValue();
    }

    public Barcode getBarcode() {
        return barcode;
    }
}
