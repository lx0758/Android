package com.liux.android.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

    /**
     * 读流
     * @param inputStream
     * @return
     */
    public static byte[] readStream(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        copyStream(inputStream, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 写流
     * @param bytes
     * @param outputStream
     */
    public static boolean writeStream(byte[] bytes, OutputStream outputStream) {
        return copyStream(new ByteArrayInputStream(bytes), outputStream);
    }

    /**
     * 拷贝流
     * @param inputStream
     * @param outputStream
     */
    public static boolean copyStream(InputStream inputStream, OutputStream outputStream) {
        try {
            int length;
            byte[] buffer = new byte[2048];
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception ignore) {}
            try {
                if (outputStream != null) outputStream.close();
            } catch (Exception ignore) {}
        }
        return false;
    }
}
