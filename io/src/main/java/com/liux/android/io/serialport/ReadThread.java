package com.liux.android.io.serialport;

import java.io.IOException;
import java.io.InputStream;

public class ReadThread extends Thread {

    private SerialPort serialPort;
    private ReadCallback readCallback;

    public ReadThread(SerialPort serialPort, ReadCallback readCallback) {
        this.serialPort = serialPort;
        this.readCallback = readCallback;
    }

    @Override
    public void run() {
        super.run();
        try {
            int size;
            byte[] buffer = new byte[4096];
            while (!isInterrupted()) {
                InputStream inputStream = serialPort.getInputStream();
                if (inputStream == null) break;
                if ((size = inputStream.read(buffer)) > 0) {
                    if (readCallback != null) {
                        byte[] bytes = new byte[size];
                        System.arraycopy(buffer, 0, bytes, 0, size);
                        readCallback.onRead(bytes);
                    }
                }
                try { Thread.sleep(200);} catch (InterruptedException ignore) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
            serialPort.close();
        }
    }

    public interface ReadCallback {

        void onRead(byte[] bytes);
    }
}
