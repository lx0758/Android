package com.liux.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompressUtil {

    public static File compressPictures(Context context, File file) {
        if (file == null) throw new NullPointerException("Compressed file cannot be empty");
        if (!file.exists() || !file.isFile() || !file.canRead()) throw new IllegalStateException("Compressed files are not operational");
        if (!isPictures(file)) return file;

        String imgPath = file.getAbsolutePath();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(imgPath);
        } catch (IOException ignore) {}

        options = new BitmapFactory.Options();
        options.inSampleSize = computeSize(srcWidth, srcHeight);
        Bitmap decodeBitmap = BitmapFactory.decodeFile(imgPath, options);
        Bitmap rotatingBitmap = rotatingImage(decodeBitmap, exifInterface);
        if (decodeBitmap != rotatingBitmap) decodeBitmap.recycle();

        File outFile = DiskUtil.getCacheTempFile(context, "jpg");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outFile);
            rotatingBitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
            fileOutputStream.flush();
            rotatingBitmap.recycle();
            return outFile;
        } catch (Exception e) {
            return file;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * 判断是否是图片
     * @param file
     * @return
     */
    private static boolean isPictures(File file) {
        String[] ss = file.getName().split("\\.");
        if (ss.length < 2) return false;
        String suffix = ss[ss.length - 1];
        switch (suffix) {
            case "bmp":
            case "gif":
            case "jpg":
            case "jpeg":
            case "png":
                return true;
            default:
                return false;
        }
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
    private static Bitmap rotatingImage(Bitmap bitmap, ExifInterface srcExif) {
        if (srcExif == null) return bitmap;
        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
        }
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
