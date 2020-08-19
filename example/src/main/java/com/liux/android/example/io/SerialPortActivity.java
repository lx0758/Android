package com.liux.android.example.io;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.R;
import com.liux.android.io.serialport.SerialPort;
import com.liux.android.io.serialport.SerialPortFinder;
import com.liux.android.tool.TT;
import com.liux.android.util.TextUtil;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SerialPortActivity extends AppCompatActivity {

    @BindView(R.id.sp_device)
    Spinner spDevice;
    @BindView(R.id.sp_baud_rate)
    Spinner spBaudRate;
    @BindView(R.id.sp_data_bit)
    Spinner spDataBit;
    @BindView(R.id.sp_stop_bit)
    Spinner spStopBit;
    @BindView(R.id.sp_parity)
    Spinner spParity;
    @BindView(R.id.btn_connection)
    Button btnConnection;
    @BindView(R.id.et_receive)
    EditText etReceive;
    @BindView(R.id.et_send)
    EditText etSend;
    @BindView(R.id.btn_send)
    Button btnSend;

    private Thread readThread;
    private SerialPort serialPort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TT.setContext(this);
        setContentView(R.layout.activity_io_serial_port);
        ButterKnife.bind(this);

        spBaudRate.setSelection(16);
        spDataBit.setSelection(3);
        spStopBit.setSelection(0);
        spParity.setSelection(2);
        etReceive.setMovementMethod(new ScrollingMovementMethod());
        btnSend.setEnabled(false);
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
        spDevice.setAdapter(adapter);
    }

    @OnClick({R.id.btn_connection, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_connection:
                if (serialPort == null) {
                    open();
                } else {
                    close();
                }
                break;
            case R.id.btn_send:
                String content = etSend.getText().toString();
                byte[] bytes = TextUtil.hex2Bytes(content);
                try {
                    serialPort.getOutputStream().write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    TT.show("写串口失败");
                }
                break;
        }
    }

    private void open() {
        String device = (String) spDevice.getSelectedItem();
        if (TextUtils.isEmpty(device)) {
            TT.show("请选择设备");
            return;
        }
        int baudRate = Integer.parseInt((String) spBaudRate.getSelectedItem());
        int dataBit = Integer.parseInt((String) spDataBit.getSelectedItem());
        int stopBit = Integer.parseInt((String) spStopBit.getSelectedItem());
        String checkBitString = (String) spParity.getSelectedItem();
        char checkBit = checkBitString.charAt(0);

        try {
            serialPort = new SerialPort(new File(device), baudRate, dataBit, stopBit, checkBit);
            readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.interrupted()) {
                            byte[] bytes = serialPort.readBytes();
                            if (bytes != null && bytes.length > 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        etReceive.getText().append(TextUtil.bytes2Hex(bytes, true)).append(' ');
                                    }
                                });
                            }
                            Thread.sleep(100);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            readThread.start();
            spDevice.setEnabled(false);
            spBaudRate.setEnabled(false);
            spDataBit.setEnabled(false);
            spStopBit.setEnabled(false);
            spParity.setEnabled(false);
            btnConnection.setText("断开连接");
            btnSend.setEnabled(true);
        } catch (IOException|SecurityException e) {
            e.printStackTrace();
            TT.show("打开串口失败");
        }
    }

    private void close() {
        if (readThread != null) readThread.interrupt();
        if (serialPort != null) serialPort.close();
        serialPort = null;
        spDevice.setEnabled(true);
        spBaudRate.setEnabled(true);
        spDataBit.setEnabled(true);
        spStopBit.setEnabled(true);
        spParity.setEnabled(true);
        btnConnection.setText("连接");
        btnSend.setEnabled(false);
    }
}
