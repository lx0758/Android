package com.liux.android.qrcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

/**
 * Created by Liux
 * Email:lx0758@qq.com
 */

public class QRCodeDecoder {
    private static final int ZOOM_MIN = 100;
    private static final int ZOOM_MAX = 3000;
    private static final int ZOOM_STEP = 200;

    /**
     * 获取解析器, 由于有两个地方要用,所以放到方法内生成
     * @return
     */
    public static MultiFormatReader getMultiFormatReader() {
        Collection<BarcodeFormat> decodeFormats = new ArrayList<>();
        decodeFormats.add(BarcodeFormat.QR_CODE);

        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        MultiFormatReader multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        return multiFormatReader;
    }

    /**
     * 解析 Bitmap 中的二维码
     * @param bitmap
     * @return
     */
    public static String decode(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) return null;

        String result = null;

        int rawSize = Math.max(bitmap.getWidth(), bitmap.getHeight());
        int reqSize = rawSize;
        if (reqSize > ZOOM_MAX) reqSize = ZOOM_MAX;

        boolean isWidth = reqSize == bitmap.getWidth();
        MultiFormatReader multiFormatReader = getMultiFormatReader();
        do {
            Bitmap compress = zoomBitmap(bitmap, reqSize, isWidth);
            result = decodeBitmap(multiFormatReader, compress);
            if (reqSize != rawSize && compress != null) compress.recycle();
            if (!TextUtils.isEmpty(result)) break;
            reqSize -= ZOOM_STEP;
        } while (reqSize > ZOOM_MIN);

        return result;
    }

    /**
     * 解析 File 中的二维码
     * @param file
     * @return
     */
    public static String decode(File file) {
        return zoomBitmapFactory(file, new Callback<File>() {
            @Override
            public void getOptions(File file, BitmapFactory.Options opts) {
                BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            }

            @Override
            public Bitmap getBitmap(File file, BitmapFactory.Options opts) {
                return BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            }
        });
    }

    /**
     * 解析 byte[] 中的二维码
     * @param bytes
     * @return
     */
    public static String decode(byte[] bytes) {
        return zoomBitmapFactory(bytes, new Callback<byte[]>() {
            @Override
            public void getOptions(byte[] bytes, BitmapFactory.Options opts) {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            }

            @Override
            public Bitmap getBitmap(byte[] bytes, BitmapFactory.Options opts) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            }
        });
    }

    /**
     * 解析 InputStream 中的二维码
     * 过时原因:很多 InputStream 不支持 reset() 方法
     * @param inputStream 必须支持 {@link InputStream#reset()} 方法,否现取不到正确的数据解码失败
     * @return
     */
    @Deprecated
    public static String decode(InputStream inputStream) {
        return zoomBitmapFactory(inputStream, new Callback<InputStream>() {
            @Override
            public void getOptions(InputStream inputStream, BitmapFactory.Options opts) {
                BitmapFactory.decodeStream(inputStream, null, opts);
            }

            @Override
            public Bitmap getBitmap(InputStream inputStream, BitmapFactory.Options opts) {
                try {
                    inputStream.reset();
                } catch (IOException ignore) {}
                return BitmapFactory.decodeStream(inputStream, null, opts);
            }
        });
    }

    /**
     * 解析 FileDescriptor 中的二维码
     * @param fileDescriptor
     * @return
     */
    public static String decode(FileDescriptor fileDescriptor) {
        return zoomBitmapFactory(fileDescriptor, new Callback<FileDescriptor>() {
            @Override
            public void getOptions(FileDescriptor fileDescriptor, BitmapFactory.Options opts) {
                BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);
            }

            @Override
            public Bitmap getBitmap(FileDescriptor fileDescriptor, BitmapFactory.Options opts) {
                return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);
            }
        });
    }

    /**
     * 按照指定大小缩放 Bitmap
     * @param bitmap
     * @param size
     * @param isWidth
     * @return
     */
    private static Bitmap zoomBitmap(Bitmap bitmap, int size, boolean isWidth) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) return null;

        int outWidth;
        int outHeight;

        if (isWidth) {
            outWidth = size;
            outHeight = size * bitmap.getHeight() / bitmap.getWidth();
        } else {
            outHeight = size;
            outWidth = size * bitmap.getWidth() / bitmap.getHeight();
        }

        return Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
    }

    /**
     * 缩放并解码所有需要 {@link BitmapFactory} 载入的对象
     * @param callback
     * @return
     */
    private static <T> String zoomBitmapFactory(T t, Callback<T> callback) {
        if (t == null) return null;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inJustDecodeBounds = true;
        callback.getOptions(t, opts);
        if (opts.outWidth == 0 || opts.outHeight == 0) return null;

        int width = opts.outWidth;
        int height = opts.outHeight;
        int rawSize = Math.max(width, height);
        int reqSize = rawSize;
        if (reqSize > ZOOM_MAX) reqSize = ZOOM_MAX;

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = calculateInSampleSize(rawSize, reqSize);

        MultiFormatReader multiFormatReader = getMultiFormatReader();
        do {
            Bitmap bitmap = callback.getBitmap(t, opts);
            if (bitmap != null) {
                String result = decodeBitmap(multiFormatReader, bitmap);
                bitmap.recycle();
                if (result != null) return result;
            }
            opts.inSampleSize *= 2;
        } while (reqSize / opts.inSampleSize > ZOOM_MIN);

        return null;
    }

    /**
     * 计算缩放因数
     *
     * InSampleSize的默认值和最小值为1（当小于1时，解码器将该值当做1来处理），且在大于1时，该值只能
     * 为2的幂（当不为2的幂时，解码器会取与该值最接近的2的幂）。例如，当inSampleSize为2时，一个
     * 2000x1000的图片，将被缩小为1000x500，相应地，它的像素数和内存占用都被缩小为了原来的1/4
     *
     * @param rawSize
     * @param reqSize
     * @return
     */
    private static int calculateInSampleSize(int rawSize, int reqSize) {
        int inSampleSize = 1;

        if (rawSize > reqSize) {
            while ((rawSize / inSampleSize) > reqSize) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 直接解码 Bitmap ,没有多余操作(缩放/变形)
     * @param bitmap
     * @return
     */
    private static String decodeBitmap(MultiFormatReader multiFormatReader, Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) return null;

        String resultString = null;

        try {
            Rect rect = new Rect();
            byte[] data = rgb2yuv420sp(bitmap, rect);
            PlanarYUVLuminanceSource planarYUVLuminanceSource = new PlanarYUVLuminanceSource(
                    data,
                    rect.right,
                    rect.bottom,
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom,
                    false
            );

            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(planarYUVLuminanceSource));
            Result result = multiFormatReader.decodeWithState(binaryBitmap);

            resultString = result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            multiFormatReader.reset();
        }

        return resultString;
    }

    /**
     * Bitmap 转换 YUV420sp 数据
     * @param bitmap
     * @return
     */
    public static byte[] rgb2yuv420sp(Bitmap bitmap, Rect rect) {
        int outWidth = bitmap.getWidth();
        int outHeight = bitmap.getHeight();

        // 需要将分辨率处理成偶数,算法才不会出问题
        boolean recycle = false;
        outWidth = outWidth % 2 == 0 ? outWidth : outWidth - 1;
        outHeight = outHeight % 2 == 0 ? outHeight : outHeight - 1;
        if (outWidth != bitmap.getWidth() || outHeight != bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, outWidth, outHeight);
            recycle = true;
        }
        outWidth = bitmap.getWidth();
        outHeight = bitmap.getHeight();
        rect.set(0, 0, outWidth, outHeight);

        // 取出 Bitmap 的 RGB 数据
        int [] argb = new int[outWidth * outHeight];
        bitmap.getPixels(argb, 0, outWidth, 0, 0, outWidth, outHeight);
        if (recycle) bitmap.recycle();

        byte [] yuv420sp = new byte[argb.length * 3 / 2];

        // RGB 转 YUV 算法
        int index = 0;
        int yIndex = 0;
        int uvIndex = argb.length;
        int A, R, G, B, Y, U, V;
        for (int j = 0; j < outHeight; j++) {
            for (int i = 0; i < outWidth; i++) {

                A = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                }

                index ++;
            }
        }

        return yuv420sp;
    }

    private interface Callback<T> {

        void getOptions(T t, BitmapFactory.Options opts);

        Bitmap getBitmap(T t, BitmapFactory.Options opts);
    }
}
