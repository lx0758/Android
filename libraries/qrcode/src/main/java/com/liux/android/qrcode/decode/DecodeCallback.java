package com.liux.android.qrcode.decode;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;

public interface DecodeCallback {

    MultiFormatReader onCreateReader();

    void onDecodeResult(Result result);

    void onLightness(float[] lightness);
}
