package com.liux.android.io.i2c;

import java.io.File;
import java.io.FileDescriptor;

public class I2C {

    public I2C(File device) throws SecurityException {
        if (1 == 1) throw new RuntimeException("Not yet implemented");
        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("su");
                String cmd = String.format("chmod 777 %s && exit\n", device.getAbsolutePath());
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) throw new SecurityException();
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
    }

    public byte[] read() {
        return null;
    }

    public void write(byte[] data) {

    }

    public void close() {

    }

    public static native FileDescriptor _open(String path);
    public static native int _read(int i2c_adr, byte[] buffer);
    public static native int _write(int i2c_adr, int sub_adr,  byte[] data);
    public static native void _close(FileDescriptor fd);
    static {
        System.loadLibrary("io-i2c");
    }
}
