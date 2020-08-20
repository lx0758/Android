/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liux.android.io.serialport;

import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SerialPort {

    public static final int BAUDRATE_50 = 50;
    public static final int BAUDRATE_75 = 75;
    public static final int BAUDRATE_110 = 110;
    public static final int BAUDRATE_134 = 134;
    public static final int BAUDRATE_150 = 150;
    public static final int BAUDRATE_200 = 200;
    public static final int BAUDRATE_300 = 300;
    public static final int BAUDRATE_600 = 600;
    public static final int BAUDRATE_1200 = 1200;
    public static final int BAUDRATE_1800 = 1800;
    public static final int BAUDRATE_2400 = 2400;
    public static final int BAUDRATE_4800 = 4800;
    public static final int BAUDRATE_9600 = 9600;
    public static final int BAUDRATE_19200 = 19200;
    public static final int BAUDRATE_38400 = 38400;
    public static final int BAUDRATE_57600 = 57600;
    public static final int BAUDRATE_115200 = 115200;
    public static final int BAUDRATE_230400 = 230400;
    public static final int BAUDRATE_460800 = 460800;
    public static final int BAUDRATE_500000 = 500000;
    public static final int BAUDRATE_576000 = 576000;
    public static final int BAUDRATE_921600 = 921600;
    public static final int BAUDRATE_1000000 = 1000000;
    public static final int BAUDRATE_1152000 = 1152000;
    public static final int BAUDRATE_1500000 = 1500000;
    public static final int BAUDRATE_2000000 = 2000000;
    public static final int BAUDRATE_2500000 = 2500000;
    public static final int BAUDRATE_3000000 = 3000000;
    public static final int BAUDRATE_3500000 = 3500000;
    public static final int BAUDRATE_4000000 = 4000000;

    public static final int DATABIT_5 = 5;
    public static final int DATABIT_6 = 6;
    public static final int DATABIT_7 = 7;
    public static final int DATABIT_8 = 8;

    public static final int STOPBIT_1 = 1;
    public static final int STOPBIT_2 = 2;

    public static final String PARITY_O = "O"; // 奇校验位
    public static final String PARITY_E = "E"; // 偶校验位
    public static final String PARITY_N = "N"; // 无校验位

    private static final String TAG = "SerialPort";

    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
	private int baudrate, dataBit, stopBit;
	private String parity;
    private FileDescriptor mFileDescriptor;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    /**
     * 打开一个串口设备
     *
     * @param device
     * @param baudrate 波特率
     * @param dataBit  数据位 (5/6/7/8)
     * @param stopBit  停止位 (1/2)
     * @param parity   校验规则 (O_奇/E_偶/N_无)
     * @throws SecurityException
     * @throws IOException
     */
    public SerialPort(File device, @BaudRate int baudrate, @DataBit int dataBit, @StopBit int stopBit, @Parity String parity) throws SecurityException, IOException {

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

        mFileDescriptor = jniOpen(device.getAbsolutePath(), baudrate, dataBit, stopBit, parity.charAt(0));
        if (mFileDescriptor == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }

        this.baudrate = baudrate;
        this.dataBit = dataBit;
        this.stopBit = stopBit;
        this.parity = parity;

        mFileInputStream = new FileInputStream(mFileDescriptor);
        mFileOutputStream = new FileOutputStream(mFileDescriptor);
    }

    public void close() {
        FileDescriptor fd = mFileDescriptor;
        mFileDescriptor = null;
        mFileInputStream = null;
        mFileOutputStream = null;
        if (fd != null) {
            jniClose(fd);
        }
    }

    public int getBaudrate() {
        return baudrate;
    }

    public int getDataBit() {
        return dataBit;
    }

    public int getStopBit() {
        return stopBit;
    }

    public String getParity() {
        return parity;
    }

    public FileDescriptor getFileDescriptor() {
        return mFileDescriptor;
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    public void write(int b) throws IOException {
        getOutputStream().write(b);
        getOutputStream().flush();
    }

    public void write(byte... b) throws IOException {
        getOutputStream().write(b);
        getOutputStream().flush();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        getOutputStream().write(b, off, len);
        getOutputStream().flush();
    }

    public int read() throws IOException {
        return getInputStream().read();
    }

    public int read(byte[] b) throws IOException {
        return getInputStream().read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return getInputStream().read(b, off, len);
    }

    public byte[] readBytes() throws IOException {
        byte[] buffer = new byte[getInputStream().available()];
        read(buffer);
        return buffer;
    }

    // JNI
    private static native FileDescriptor jniOpen(String path, int baudrate, int dataBit, int stopBit, char parity);

    private static native void jniClose(FileDescriptor fd);

    static {
        System.loadLibrary("io-serialport");
    }

    @IntDef({
            BAUDRATE_50,
            BAUDRATE_75,
            BAUDRATE_110,
            BAUDRATE_134,
            BAUDRATE_150,
            BAUDRATE_200,
            BAUDRATE_300,
            BAUDRATE_600,
            BAUDRATE_1200,
            BAUDRATE_1800,
            BAUDRATE_2400,
            BAUDRATE_4800,
            BAUDRATE_9600,
            BAUDRATE_19200,
            BAUDRATE_38400,
            BAUDRATE_57600,
            BAUDRATE_115200,
            BAUDRATE_230400,
            BAUDRATE_460800,
            BAUDRATE_500000,
            BAUDRATE_576000,
            BAUDRATE_921600,
            BAUDRATE_1000000,
            BAUDRATE_1152000,
            BAUDRATE_1500000,
            BAUDRATE_2000000,
            BAUDRATE_2500000,
            BAUDRATE_3000000,
            BAUDRATE_3500000,
            BAUDRATE_4000000
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BaudRate {
    }

    @IntDef({DATABIT_5, DATABIT_6, DATABIT_7, DATABIT_8})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DataBit {
    }

    @IntDef({STOPBIT_1, STOPBIT_2})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StopBit {
    }

    @StringDef({PARITY_O, PARITY_E, PARITY_N})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Parity {}
}
