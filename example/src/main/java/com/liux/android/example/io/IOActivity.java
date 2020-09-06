package com.liux.android.example.io;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.databinding.ActivityIoBinding;

public class IOActivity extends AppCompatActivity {

    private ActivityIoBinding mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityIoBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.btnSerialPort.setOnClickListener(view -> {
            startActivity(new Intent(this, SerialPortActivity.class));
        });
        mViewBinding.btnGpio.setOnClickListener(view -> {
            startActivity(new Intent(this, GpioActivity.class));
        });
        mViewBinding.btnI2c.setOnClickListener(view -> {
            startActivity(new Intent(this, I2CActivity.class));
        });
    }
}
