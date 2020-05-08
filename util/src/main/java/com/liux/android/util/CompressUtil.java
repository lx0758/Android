package com.liux.android.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.liux.android.util.extra.ExtraExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 仿微信压缩参数的图片压缩工具类
 */
public class CompressUtil {

    /**
     * 图片压缩
     * @param in
     * @return
     */
    public static byte[] compressPictures(byte[] in) {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            inputStream = new ByteArrayInputStream(in);
            byteArrayOutputStream = new ByteArrayOutputStream();
            compressPictures(inputStream, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (byteArrayOutputStream != null) byteArrayOutputStream.close();
            } catch (Exception ignore) {}
        }
        return null;
    }

    /**
     * 图片压缩
     * @param in
     * @param out
     * @return
     */
    public static boolean compressPictures(File in, File out) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(in);
            outputStream = new FileOutputStream(out);
            return compressPictures(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (Exception ignore) {}
        }
        return false;
    }

    /**
     * 图片压缩
     * @param in
     * @param out
     * @return
     */
    public static boolean compressPictures(InputStream in, OutputStream out) {
        if (in == null) throw new NullPointerException("Input can not be empty");
        if (out == null) throw new NullPointerException("Output cannot be empty");

        byte[] inData = DiskUtil.readStream(in);
        if (inData == null) throw new NullPointerException("Input read failed");

        ExtraExifInterface extraExifInterface = null;
        try {
            extraExifInterface = new ExtraExifInterface(new ByteArrayInputStream(inData));
        } catch (IOException ignore) {}

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(inData, 0, inData.length, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        options = new BitmapFactory.Options();
        options.inSampleSize = computeSize(srcWidth, srcHeight);
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        Bitmap decodeBitmap = BitmapFactory.decodeByteArray(inData, 0, inData.length, options);
        Bitmap rotatingBitmap = rotatingImage(decodeBitmap, extraExifInterface);
        if (decodeBitmap != rotatingBitmap) decodeBitmap.recycle();

        rotatingBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
        rotatingBitmap.recycle();
        return true;
    }

    /**
     * 计算压缩比
     * @param srcWidth
     * @param srcHeight
     * @return
     */
    private static int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;
        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);
        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide >= 1664 && longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    /**
     * 计算角度
     * @param bitmap
     * @param srcExif
     * @return
     */
    private static Bitmap rotatingImage(Bitmap bitmap, ExtraExifInterface srcExif) {
        if (srcExif == null) return bitmap;
        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExtraExifInterface.TAG_ORIENTATION, ExtraExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExtraExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExtraExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExtraExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
        }
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
