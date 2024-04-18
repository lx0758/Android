package com.liux.android.example.io;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.databinding.ActivityIoSerialPortBinding;
import com.liux.android.io.Shell;
import com.liux.android.io.serialport.SerialPort;
import com.liux.android.io.serialport.SerialPortFinder;
import com.liux.android.tool.TT;
import com.liux.android.util.TextUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SerialPortActivity extends AppCompatActivity {

    private ActivityIoSerialPortBinding mViewBinding;

    private Thread readThread;
    private SerialPort serialPort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        mViewBinding = ActivityIoSerialPortBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.spBaudRate.setSelection(16);
        mViewBinding.spDataBit.setSelection(3);
        mViewBinding.spStopBit.setSelection(0);
        mViewBinding.spParity.setSelection(2);
        mViewBinding.etReceive.setMovementMethod(new ScrollingMovementMethod());
        mViewBinding.btnSend.setEnabled(false);

        mViewBinding.btnConnection.setOnClickListener(view -> {
            if (serialPort == null) {
                open();
            } else {
                close();
            }
        });
        mViewBinding.btnSend.setOnClickListener(view -> {
            String content = mViewBinding.etSend.getText().toString();
            byte[] bytes = TextUtil.hex2Bytes(content);
            try {
                serialPort.getOutputStream().write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                TT.show("写串口失败");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SpinnerAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, new SerialPortFinder().getAllDevicesPath());
        mViewBinding.spDevice.setAdapter(adapter);
    }

    private void open() {
        String device = (String) mViewBinding.spDevice.getSelectedItem();
        if (TextUtils.isEmpty(device)) {
            TT.show("请选择设备");
            return;
        }
        int baudRate = Integer.parseInt((String) mViewBinding.spBaudRate.getSelectedItem());
        int dataBit = Integer.parseInt((String) mViewBinding.spDataBit.getSelectedItem());
        int stopBit = Integer.parseInt((String) mViewBinding.spStopBit.getSelectedItem());
        String parity = (String) mViewBinding.spParity.getSelectedItem();
        try {
            serialPort = new SerialPort(Shell.DEFAULT, new File(device), baudRate, dataBit, stopBit, parity, 1, 0);
            readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int len;
                        byte[] buffer = new byte[2048];
                        while (!Thread.interrupted()) {
                            if ((len = serialPort.read(buffer)) != -1) {
                                byte[] bytes = Arrays.copyOf(buffer, len);
                                runOnUiThread(() -> mViewBinding.etReceive.getText().append(TextUtil.bytes2Hex(bytes, true)).append(' '));
                            }
                            Thread.sleep(0);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            readThread.start();
            mViewBinding.spDevice.setEnabled(false);
            mViewBinding.spBaudRate.setEnabled(false);
            mViewBinding.spDataBit.setEnabled(false);
            mViewBinding.spStopBit.setEnabled(false);
            mViewBinding.spParity.setEnabled(false);
            mViewBinding.btnConnection.setText("断开连接");
            mViewBinding.btnSend.setEnabled(true);
        } catch (IOException|SecurityException e) {
            e.printStackTrace();
            TT.show("打开串口失败");
        }
    }

    private void close() {
        if (readThread != null) readThread.interrupt();
        if (serialPort != null) serialPort.close();
        serialPort = null;
        mViewBinding.spDevice.setEnabled(true);
        mViewBinding.spBaudRate.setEnabled(true);
        mViewBinding.spDataBit.setEnabled(true);
        mViewBinding.spStopBit.setEnabled(true);
        mViewBinding.spParity.setEnabled(true);
        mViewBinding.btnConnection.setText("连接");
        mViewBinding.btnSend.setEnabled(false);
    }
}
