package com.liux.android.qrcode.decode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.liux.android.qrcode.camrea.PreviewFrame;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class DecodeHandler extends Handler {

    public static DecodeHandler create(final DecodeCallback decodeCallback) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final DecodeHandler[] decodeHandler = new DecodeHandler[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                decodeHandler[0] = new DecodeHandler(decodeCallback);
                countDownLatch.countDown();
                Looper.loop();
            }
        },"Therad-Decoder").start();
        try {
            countDownLatch.await();
        } catch (InterruptedException ignore) {}
        return decodeHandler[0];
    }

    private boolean mCanRun = true;
    private boolean mProcessing = false;

    private int mLightnessIndex = 0;
    private float[] mLightnessList = new float[]{1F, 1F, 1F};

    private DecodeCallback mDecodeCallback;
    private MultiFormatReader mMultiFormatReader;

    private DecodeHandler(DecodeCallback decodeCallback) {
        mDecodeCallback = decodeCallback;
        reset();
    }

    @Override
    public void handleMessage(Message msg) {
        if (!mCanRun) return;
        mProcessing = true;

        long time = System.currentTimeMillis();

        PreviewFrame previewFrame = (PreviewFrame) msg.obj;
        Result result = decode(previewFrame);
        if (mCanRun && result != null) {
            mCanRun = false;
            mDecodeCallback.onDecodeResult(result);
        }

        float lightness = lightness(previewFrame);
        mLightnessIndex = mLightnessIndex % mLightnessList.length;
        mLightnessList[mLightnessIndex] = lightness;
        mLightnessIndex ++;
        if (mCanRun) {
            mDecodeCallback.onLightness(mLightnessList);
        }

        Log.d("DecodeHandler", String.format(
                "parsing frame: %dx%d, time: %dms, lightness: %s, text: %s",
                previewFrame.getWidth(),
                previewFrame.getHeight(),
                System.currentTimeMillis() - time,
                Arrays.toString(mLightnessList),
                result == null ? null : result.getText()
        ));

        mProcessing = false;
    }

    /**
     * 重置
     */
    public void reset() {
        mCanRun = true;
        mMultiFormatReader = null;
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
        getLooper().quit();
    }

    /**
     * 解码 YUV 帧
     * @param previewFrame
     * @return
     */
    private Result decode(PreviewFrame previewFrame) {
        Result result = null;

        if (mMultiFormatReader == null) {
            mMultiFormatReader = mDecodeCallback.onCreateReader();
        }

        try {
            PlanarYUVLuminanceSource planarYUVLuminanceSource = new PlanarYUVLuminanceSource(
                    previewFrame.getYuv(),
                    previewFrame.getWidth(),
                    previewFrame.getHeight(),
                    0,
                    0,
                    previewFrame.getWidth(),
                    previewFrame.getHeight(),
                    false
            );
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(planarYUVLuminanceSource));
            result = mMultiFormatReader.decodeWithState(binaryBitmap);
        } catch (Exception ignore) {} finally {
            mMultiFormatReader.reset();
        }

        return  result;
    }

    /**
     * 计算 YUV 帧亮度
     * https://blog.csdn.net/bluewindtalker/article/details/79999172
     * @param previewFrame
     * @return 取值 [0,1] 0_暗 1_亮
     */
    private float lightness(PreviewFrame previewFrame) {
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
