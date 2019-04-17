package com.liux.android.example.serialport;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import android_serialport_api.SerialPortFinder;

public class SerialPortActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = new TextView(this);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        marginLayoutParams.setMargins(100, 100, 100, 100);
        setContentView(textView, marginLayoutParams);

        SerialPortFinder serialPortFinder = new SerialPortFinder();
        String[] strings = serialPortFinder.getAllDevicesPath();
        if (strings == null || strings.length == 0) {
            textView.setText("没有找到串口设备");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder().append("找到串口设备:").append('\n');
        for (String path : strings) {
            stringBuilder.append(path).append('\n');
        }
        textView.setText(stringBuilder.toString());
    }
}
