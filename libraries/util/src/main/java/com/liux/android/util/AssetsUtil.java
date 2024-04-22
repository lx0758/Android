package com.liux.android.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetsUtil {

    /**
     * 写出到文件
     * @param context
     * @param assetsPath
     * @param target
     */
    public static void writeAssetsToFile(Context context, String assetsPath, File target) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.getAssets().open(assetsPath);
            out = new FileOutputStream(target);
            int length;
            byte[] buffer = new byte[2048];
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
