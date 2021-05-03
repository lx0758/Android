package com.liux.android.qrcode.decode;

import com.liux.android.qrcode.QRCode;

import java.util.List;

public interface DecodeCallback {

    void onDecodeResult(List<QRCode> qrCodes);

    void onLightness(float lightness);
}
