package com.liux.android.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static void zip(File input, File output) {
        try {
            zip(
                    new FileInputStream(input),
                    new FileOutputStream(output)
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static byte[] zip(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        zip(
                new ByteArrayInputStream(data),
                byteArrayOutputStream
        );
        return byteArrayOutputStream.toByteArray();
    }

    public static void zip(InputStream inputStream, OutputStream outputStream) {
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(outputStream);
            int len = 0;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, len);
            }
            zipOutputStream.finish();
            zipOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipOutputStream != null) try { zipOutputStream.close(); } catch (IOException e) {};
            if (inputStream != null) try { inputStream.close(); } catch (IOException e) {};
            if (outputStream != null) try { outputStream.close(); } catch (IOException e) {};
        }
    }

    public static void unzip(File input, File output) {
        try {
            unzip(
                    new FileInputStream(input),
                    new FileOutputStream(output)
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static byte[] unzip(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        unzip(
                new ByteArrayInputStream(data),
                byteArrayOutputStream
        );
        return byteArrayOutputStream.toByteArray();
    }

    public static void unzip(InputStream inputStream, OutputStream outputStream) {
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(inputStream);
            int len = 0;
            byte[] buffer = new byte[4096];
            while ((len = zipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipInputStream != null) try { zipInputStream.close(); } catch (IOException e) {};
            if (inputStream != null) try { inputStream.close(); } catch (IOException e) {};
            if (outputStream != null) try { outputStream.close(); } catch (IOException e) {};
        }
    }

    public static void gzip(File input, File output) {
        try {
            gzip(
                    new FileInputStream(input),
                    new FileOutputStream(output)
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static byte[] gzip(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        gzip(
                new ByteArrayInputStream(data),
                byteArrayOutputStream
        );
        return byteArrayOutputStream.toByteArray();
    }

    public static void gzip(InputStream inputStream, OutputStream outputStream) {
        GZIPOutputStream gzipOutputStream = null;
        try {
            gzipOutputStream = new GZIPOutputStream(outputStream);
            int len = 0;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                gzipOutputStream.write(buffer, 0, len);
            }
            gzipOutputStream.finish();
            gzipOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzipOutputStream != null) try { gzipOutputStream.close(); } catch (IOException e) {};
            if (inputStream != null) try { inputStream.close(); } catch (IOException e) {};
            if (outputStream != null) try { outputStream.close(); } catch (IOException e) {};
        }
    }

    public static void ungzip(File input, File output) {
        try {
            ungzip(
                    new FileInputStream(input),
                    new FileOutputStream(output)
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static byte[] ungzip(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ungzip(
                new ByteArrayInputStream(data),
                byteArrayOutputStream
        );
        return byteArrayOutputStream.toByteArray();
    }

    public static void ungzip(InputStream inputStream, OutputStream outputStream) {
        GZIPInputStream gzipInputStream = null;
        try {
            gzipInputStream = new GZIPInputStream(inputStream);
            int len = 0;
            byte[] buffer = new byte[4096];
            while ((len = gzipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzipInputStream != null) try { gzipInputStream.close(); } catch (IOException e) {};
            if (inputStream != null) try { inputStream.close(); } catch (IOException e) {};
            if (outputStream != null) try { outputStream.close(); } catch (IOException e) {};
        }
    }
}
