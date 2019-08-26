package com.liux.android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.*;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liux on 2016/9/7.
 */
public class ImageUtil {

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    public static List<String> getAllImagePaths(Context context, int maxCount) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = context.getContentResolver();

        Cursor cursor = mContentResolver.query(uri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png", "image/bmp"},
                MediaStore.Images.Media.DATE_MODIFIED);

        List<String> paths = new ArrayList<>();
        if (cursor == null) return paths;
        if (cursor.moveToFirst()) {
            do {
                String path = cursor.getString(0);
                paths.add(path);
                if (maxCount != 0 && paths.size() >= maxCount) break;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return paths;
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    public static List<String> getFolderImagePaths(Context context, String folder) {
        List<String> paths = getAllImagePaths(context, Integer.MAX_VALUE);

        List<String> result = new ArrayList<>();
        for (String path : paths) {
            if (path.startsWith(folder) && !path.replace(folder + "/", "").contains("/")) {
                result.add(path);
            }
        }
        return result;
    }

    /**
     * 保存Bitmap至File
     * @param bitmap
     * @param file
     * @return
     */
    public static boolean saveBitmap(Bitmap bitmap, File file) {
        OutputStream outputStream = null;
        try {
            if (file.exists()) file.delete();
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException ignore) {}
        }
        return true;
    }

    /**
     * 拷贝图片扩展信息
     * @param oldFilePath
     * @param newFilePath
     * @return
     */
    public static boolean copyExifInterface(String oldFilePath, String newFilePath) {
        try {
            ExifInterface oldExif = new ExifInterface(oldFilePath);
            ExifInterface newExif = new ExifInterface(newFilePath);
            Class clazz = ExifInterface.class;
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                if (field.getType() == ExifInterface.ExifTag[].class) {
                    ExifInterface.ExifTag[] exifTags = (ExifInterface.ExifTag[]) field.get(clazz);
                    for (ExifInterface.ExifTag exifTag : exifTags) {
                        String attribute = oldExif.getAttribute(exifTag.name);
                        newExif.setAttribute(exifTag.name, attribute);
                    }
                }
            }
            newExif.saveAttributes();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 图片模糊效果
     * @param context
     * @param bitmap
     * @param radius
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(context);

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, bitmap);

        blurScript.setRadius(radius);

        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        allOut.copyTo(outBitmap);

        bitmap.recycle();
        rs.destroy();

        return outBitmap;
    }

    /**
     * 图片黑白效果
     * @param srcBitmap
     * @return
     */
    public static Bitmap matrixBitmap(Bitmap srcBitmap) {
        float[] src = new float[]{
                0.28F, 0.60F, 0.40F, 0, 0,
                0.28F, 0.60F, 0.40F, 0, 0,
                0.28F, 0.60F, 0.40F, 0, 0,
                0, 0, 0, 1, 0,
        };
        ColorMatrix cm = new ColorMatrix(src);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        Bitmap resultBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(100);
        paint.setColorFilter(f);
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
        return resultBitmap;
    }

    /**
     * 等比压缩图像
     * @param source
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap compressScale(Bitmap source, int maxWidth, int maxHeight) {
        if (source == null) return null;

        int width = source.getWidth();
        int height = source.getHeight();
        if (width == 0 || height == 0) return source;

        if (width <= maxHeight && height <= maxHeight) return source;

        float widthScale = (maxWidth + 0.0F) / width;
        float heightScale = (maxHeight + 0.0F) / height;
        float scale = Math.min(widthScale, heightScale);

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
    }

    /**
     * 获取位图的像素字节数组
     * @param source
     * @param format
     * @param quality
     * @return
     */
    public static byte[] getBitmapBytes(Bitmap source, Bitmap.CompressFormat format, int quality) {
        if (source == null) return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        source.compress(format, quality, outputStream);
        return outputStream.toByteArray();
    }
}
