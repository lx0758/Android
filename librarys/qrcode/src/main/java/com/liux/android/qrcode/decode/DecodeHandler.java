package com.liux.android.qrcode.decode;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.liux.android.qrcode.QRCode;
import com.liux.android.qrcode.camrea.PreviewFrame;

import java.util.List;

public class DecodeHandler extends Handler {
    public static boolean DEBUG = true;

    public static DecodeHandler create(final DecodeCallback decodeCallback) {
        HandlerThread handlerThread = new HandlerThread(DecodeHandler.class.getSimpleName());
        handlerThread.start();
        return new DecodeHandler(handlerThread.getLooper(), decodeCallback);
    }

    private boolean mCanRun = true;
    private boolean mProcessing = false;

    private int mLightnessIndex = 0;
    private final float[] mLightnessList = new float[]{1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F};

    private final DecodeCallback mDecodeCallback;

    private final BarcodeScanner mBarcodeScanner;

    public DecodeHandler(@NonNull Looper looper, DecodeCallback decodeCallback) {
        super(looper);
        mDecodeCallback = decodeCallback;
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();
        mBarcodeScanner = BarcodeScanning.getClient(options);
    }

    @Override
    public void handleMessage(Message msg) {
        if (!mCanRun) return;
        mProcessing = true;

        final PreviewFrame previewFrame = (PreviewFrame) msg.obj;

        if (mCanRun) {
            InputImage inputImage = InputImage.fromByteArray(
                    previewFrame.getYuv(),
                    previewFrame.getWidth(),
                    previewFrame.getHeight(),
                    previewFrame.getAngle(),
                    InputImage.IMAGE_FORMAT_YV12
            );
            mBarcodeScanner.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                @Override
                public void onSuccess(List<Barcode> barcodes) {
                    if (barcodes == null || barcodes.isEmpty()) return;
                    if (mCanRun) {
                        mCanRun = false;
                        mDecodeCallback.onDecodeResult(QRCode.from(previewFrame, barcodes));
                    }
                }
            });
        }

        float previewLightness = calculateLightness(previewFrame);
        mLightnessIndex = mLightnessIndex % mLightnessList.length;
        mLightnessList[mLightnessIndex] = previewLightness;
        mLightnessIndex ++;
        if (mCanRun) {
            // 取采样亮度平均值
            float lightnessCount = 0;
            for (float light : mLightnessList) {
                lightnessCount += light;
            }
            float lightness = lightnessCount / mLightnessList.length;
            mDecodeCallback.onLightness(lightness);
        }

        mProcessing = false;
    }

    /**
     * 重置
     */
    public void prepare() {
        mCanRun = true;
        mProcessing = false;
    }

    /**
     * 解码帧
     * @param previewFrame
     */
    public void resolve(PreviewFrame previewFrame) {
        if (!mCanRun || mProcessing) return;
        Message message = obtainMessage();
        message.obj = previewFrame;
        sendMessage(message);
    }

    /**
     * 销毁
     */
    public void destroy() {
        mCanRun = false;
        mProcessing = false;
        getLooper().quit();
        mBarcodeScanner.close();
    }

    /**
     * 计算 YUV 帧亮度
     * https://blog.csdn.net/bluewindtalker/article/details/79999172
     * @param previewFrame
     * @return 取值 [0,1] 0_暗 1_亮
     */
    private float calculateLightness(PreviewFrame previewFrame) {
        // yuv.length - pixeCount * 1.5f 的目是判断图像格式是不是YUV420格式，只有是这种格式才相等
        // 因为 int 整形与float浮点直接比较会出问题，所以用 0.00001F 比
        byte[] yuv = previewFrame.getYuv();
        long pixeCount = previewFrame.getWidth() * previewFrame.getHeight();
        if (Math.abs(yuv.length - pixeCount * 1.5F) > 0.00001F) return 1;

        int step = 10;
        long lightnessCount = 0L;
        for (int i = 0; i < pixeCount; i += step) {
            // 如果直接加是不行的，因为 yuv[i] 记录的是色值并不是数值，byte 的范围是 -128 到 +127，
            // 而亮度 0xFF 是 0b11111111 是 -127， 所以这里需要先转为 unsigned long 参考 Byte.toUnsignedLong()
            lightnessCount += yuv[i] & 0xFFL;
        }
        long lightness = lightnessCount / (pixeCount / step);

        return lightness / 255.0F;
    }
}
