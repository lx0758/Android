package com.liux.android.example.io;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.R;
import com.liux.android.io.gpio.Gpio;
import com.liux.android.tool.TT;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class GpioActivity extends AppCompatActivity {

    @BindView(R.id.et_number)
    EditText etNumber;
    @BindView(R.id.sp_direction)
    Spinner spDirection;
    @BindView(R.id.sp_edge)
    Spinner spEdge;
    @BindView(R.id.ll_edge)
    LinearLayout llEdge;
    @BindView(R.id.btn_open)
    Button btnOpen;
    @BindView(R.id.ll_operate)
    LinearLayout llOperate;
    @BindView(R.id.btn_high)
    Button btnHigh;
    @BindView(R.id.btn_low)
    Button btnLow;
    @BindView(R.id.btn_get)
    Button btnGet;
    @BindView(R.id.et_logs)
    EditText etLogs;

    private Gpio gpio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TT.setContext(this);
        setContentView(R.layout.activity_io_gpio);
        ButterKnife.bind(this);

        llEdge.setVisibility(View.GONE);
        llOperate.setVisibility(View.GONE);
        btnHigh.setEnabled(false);
        btnLow.setEnabled(false);
        btnGet.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
    }

    @OnClick({R.id.btn_open, R.id.btn_high, R.id.btn_low, R.id.btn_get})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                if (gpio == null) {
                    open();
                } else {
                    close();
                }
                break;
            case R.id.btn_high:
                if (gpio != null) gpio.set(Gpio.VALUE_HIGH);
                printLn(false, Gpio.VALUE_HIGH);
                break;
            case R.id.btn_low:
                if (gpio != null) gpio.set(Gpio.VALUE_LOW);
                printLn(false, Gpio.VALUE_LOW);
                break;
            case R.id.btn_get:
                if (gpio == null) return;
                printLn(true, gpio.get());
                break;
        }
    }

    @OnItemSelected(R.id.sp_direction)
    void onItemSelected(int position) {
        switch (position) {
            case 0:
                llEdge.setVisibility(View.VISIBLE);
                llOperate.setVisibility(View.GONE);
                break;
            case 1:
                llEdge.setVisibility(View.GONE);
                llOperate.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void open() {
        int number = -1;
        try {
            number = Integer.valueOf(etNumber.getText().toString());
        } catch (Exception ignore) {}
        if (number < 0) {
            TT.show("请正确输入GPIO导出序号");
            return;
        }
        String direction = (String) spDirection.getSelectedItem();
        String edge = (String) spEdge.getSelectedItem();
        try {
            Gpio gpio = new Gpio(number, direction, edge);
            gpio.setCallback(new Gpio.Callback() {
                @Override
                public void onEvent(int type, int value) {
                    printLn(true, value);
                }
            });
            gpio.open();
            this.gpio = gpio;

            etNumber.setEnabled(false);
            spDirection.setEnabled(false);
            spEdge.setEnabled(false);

            btnHigh.setEnabled(true);
            btnLow.setEnabled(true);
            btnGet.setEnabled(true);

            btnOpen.setText("关闭");
        } catch (IOException |SecurityException e) {
            e.printStackTrace();
            TT.show("打开端口失败");
        }
    }

    private void close() {
        if (gpio != null) gpio.close();
        gpio = null;

        etNumber.setEnabled(true);
        spDirection.setEnabled(true);
        spEdge.setEnabled(true);

        btnHigh.setEnabled(false);
        btnLow.setEnabled(false);
        btnGet.setEnabled(false);

        btnOpen.setText("打开");
    }

    private void printLn(boolean direction, int value) {
        etLogs.post(new Runnable() {
            @Override
            public void run() {
                etLogs.append(String.format(Locale.CHINA, "信号方向：%s，电平值：%s\n", direction ? "输入" : "输出", value == 0 ? "低" : "高"));
            }
        });
    }
}
