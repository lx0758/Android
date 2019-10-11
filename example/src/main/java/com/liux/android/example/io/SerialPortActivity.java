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
import com.liux.android.io.serialport.ReadThread;
import com.liux.android.io.serialport.SerialPort;
import com.liux.android.io.serialport.SerialPortFinder;
import com.liux.android.tool.TT;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

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
    @BindView(R.id.sp_check_bit)
    Spinner spCheckBit;
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
        spCheckBit.setSelection(2);
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
                try {
                    serialPort.getOutputStream().write(content.getBytes());
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
        int baudRate = Integer.valueOf((String) spBaudRate.getSelectedItem());
        int dataBit = Integer.valueOf((String) spDataBit.getSelectedItem());
        int stopBit = Integer.valueOf((String) spStopBit.getSelectedItem());
        String checkBitString = (String) spCheckBit.getSelectedItem();
        char checkBit = checkBitString.charAt(0);

        try {
            serialPort = new SerialPort(new File(device), baudRate, dataBit, stopBit, checkBit);
            readThread = new ReadThread(serialPort, new ReadThread.ReadCallback() {
                // 这里没有处理串口通信协议,所以全部展示数据
                // 正常情况会根据串口协议进行数据截断
                private ByteBuffer byteBuffer = ByteBuffer.allocate(0xFFFF);
                @Override
                public void onRead(byte[] bytes) {
                    try {
                        byteBuffer.put(bytes);
                    } catch (Exception e) {
                        byteBuffer.clear();
                        byteBuffer.put(bytes);
                    }
                    String content = new String(byteBuffer.array());
                    etReceive.post(new Runnable() {
                        @Override
                        public void run() {
                            etReceive.setText(content);
                        }
                    });
                }
            });
            readThread.start();
            spDevice.setEnabled(false);
            spBaudRate.setEnabled(false);
            spDataBit.setEnabled(false);
            spStopBit.setEnabled(false);
            spCheckBit.setEnabled(false);
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
        spCheckBit.setEnabled(true);
        btnConnection.setText("连接");
        btnSend.setEnabled(false);
    }
}
