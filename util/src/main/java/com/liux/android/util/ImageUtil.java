package com.liux.android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = context.getContentResolver();

        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png", "image/bmp"},
                MediaStore.Images.Media.DATE_MODIFIED);

        List<String> paths = new ArrayList();
        if (cursor != null) {
            if (cursor.moveToLast()) {
                paths = new ArrayList();

                while (true) {
                    String path = cursor.getString(0);
                    paths.add(path);

                    if (!cursor.moveToPrevious()) break;
                    if (maxCount != 0 && paths.size() >= maxCount) break;
                }
            }
            cursor.close();
        }
        return paths;
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    public static List<String> getFolderImagePaths(Context context, String folder) {
        List<String> paths = getAllImagePaths(context, Integer.MAX_VALUE);

        List<String> result = new ArrayList();
        for (String path : paths) {
            if (path.startsWith(folder) && path.replace(folder + "/", "").indexOf("/") == -1) {
                result.add(path);
            }
        }
        return result;
    }

    /**
     * 保存Bitmap至Uri
     * @param bitmap
     * @param uri
     * @return
     */
    public static boolean saveBitmap(Bitmap bitmap, Uri uri) {
        File file = new File(uri.getPath());
        try {
            if (file.exists()) file.delete();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream out = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
            Class<ExifInterface> cls = ExifInterface.class;
            Field[] fields = cls.getFields();
            for (int i = 0; i < fields.length; i++) {
                String fieldName = fields[i].getName();
                if (!TextUtils.isEmpty(fieldName) && fieldName.startsWith("TAG")) {
                    String fieldValue = fields[i].get(cls).toString();
                    String attribute = oldExif.getAttribute(fieldValue);
                    if (attribute != null) {
                        newExif.setAttribute(fieldValue, attribute);
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
}
