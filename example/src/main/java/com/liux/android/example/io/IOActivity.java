package com.liux.android.example.io;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liux.android.example.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class IOActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_serial_port, R.id.btn_gpio, R.id.btn_i2c})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_serial_port:
                startActivity(new Intent(this, SerialPortActivity.class));
                break;
            case R.id.btn_gpio:
                startActivity(new Intent(this, GpioActivity.class));
                break;
            case R.id.btn_i2c:
                startActivity(new Intent(this, I2CActivity.class));
                break;
        }
    }
}
