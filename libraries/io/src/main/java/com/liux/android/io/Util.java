package com.liux.android.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util {

    public static String readFile(File file) throws SecurityException {
        if (!file.canRead()) throw new SecurityException();
        String result = null;
        try(
                FileInputStream inputStream = new FileInputStream(file);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ) {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            result = outputStream.toString();
        } catch (IOException ignored) {}
        return result;
    }

    public static boolean writeFile(File file, String value) throws SecurityException {
        if (!file.canWrite()) throw new SecurityException();
        boolean result = false;
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(value.getBytes());
            result = true;
        } catch (IOException ignored) {}
        return result;
    }
}
