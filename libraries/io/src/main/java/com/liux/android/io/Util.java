package com.liux.android.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util {

    public static String readFile(File file) throws IOException {
        if (!file.canRead()) throw new IOException("file can't read");
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
        }
        return result;
    }

    public static boolean writeFile(File file, String value) throws IOException {
        if (!file.canWrite()) throw new IOException("file can't write");
        boolean result = false;
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(value.getBytes());
            result = true;
        }
        return result;
    }
}
