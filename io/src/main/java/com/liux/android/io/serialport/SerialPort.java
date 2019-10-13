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

	public static final char CHECKBIT_O = 'O'; // 奇校验位
	public static final char CHECKBIT_E = 'E'; // 偶校验位
	public static final char CHECKBIT_N = 'N'; // 无校验位
	public static final char CHECKBIT_S = ' '; // 空校验位

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public static SerialPort open(File device, @BaudRate int baudrate, @DataBit int databit, @StopBit int stopbit, /*@CheckBit*/ char checkbit) {
		SerialPort serialPort = null;
		try {
			serialPort = new SerialPort(device, baudrate, databit, stopbit, checkbit);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return serialPort;
	}

	/**
	 * 打开一个串口设备
	 * @param device
	 * @param baudrate 波特率
	 * @param databit 数据位 (5/6/7/8)
	 * @param stopbit 停止位 (1/2)
	 * @param checkbit 校验规则 (O_奇/E_偶/N_无)
	 * @throws SecurityException
	 * @throws IOException
	 */
	public SerialPort(File device, @BaudRate int baudrate, @DataBit int databit, @StopBit int stopbit, /*@CheckBit*/ char checkbit) throws SecurityException, IOException {

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

		mFd = _open(device.getAbsolutePath(), baudrate, databit, stopbit, checkbit);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	public void close() {
		FileDescriptor fd = mFd;
		mFd = null;
		mFileInputStream = null;
		mFileOutputStream = null;
		_close(fd);
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	private static native FileDescriptor _open(String path, int baudrate, int databit, int stopbit, char checkrule);
	private static native void _close(FileDescriptor fd);
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
	public @interface BaudRate {}

	@IntDef({DATABIT_5, DATABIT_6, DATABIT_7, DATABIT_8})
	@Retention(RetentionPolicy.SOURCE)
	public @interface DataBit {}

	@IntDef({STOPBIT_1, STOPBIT_2})
	@Retention(RetentionPolicy.SOURCE)
	public @interface StopBit {}

	//@StringDef({CHECKBIT_O, CHECKBIT_E, CHECKBIT_N, CHECKBIT_S})
	//@Retention(RetentionPolicy.SOURCE)
	//public @interface CheckBit {}
}
