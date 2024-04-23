package com.liux.android.example.io;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.databinding.ActivityIoGpioBinding;
import com.liux.android.io.gpio.Gpio;
import com.liux.android.tool.Shell;
import com.liux.android.tool.TT;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class GpioActivity extends AppCompatActivity {

    private ActivityIoGpioBinding mViewBinding;

    private Gpio gpio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityIoGpioBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.llEdge.setVisibility(View.GONE);
        mViewBinding.llOperate.setVisibility(View.GONE);
        mViewBinding.btnHigh.setEnabled(false);
        mViewBinding.btnLow.setEnabled(false);
        mViewBinding.btnGet.setEnabled(false);

        mViewBinding.btnOpen.setOnClickListener(view -> {
            if (gpio == null) {
                open();
            } else {
                close();
            }
        });
        mViewBinding.btnHigh.setOnClickListener(view -> {
            if (gpio != null) gpio.set(Gpio.VALUE_HIGH);
            println("设置", Gpio.VALUE_HIGH);
        });
        mViewBinding.btnLow.setOnClickListener(view -> {
            if (gpio != null) gpio.set(Gpio.VALUE_LOW);
            println("设置", Gpio.VALUE_LOW);
        });
        mViewBinding.btnGet.setOnClickListener(view -> {
            if (gpio == null) return;
            println("查询", gpio.get());
        });
        mViewBinding.spDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mViewBinding.llEdge.setVisibility(View.VISIBLE);
                        mViewBinding.llOperate.setVisibility(View.GONE);
                        break;
                    case 1:
                        mViewBinding.llEdge.setVisibility(View.GONE);
                        mViewBinding.llOperate.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
    }

    private void open() {
        int number = -1;
        try {
            number = Integer.parseInt(mViewBinding.etNumber.getText().toString());
        } catch (Exception ignore) {}
        if (number < 0) {
            TT.show("请正确输入GPIO导出序号");
            return;
        }
        String direction = (String) mViewBinding.spDirection.getSelectedItem();
        String edge = (String) mViewBinding.spEdge.getSelectedItem();
        try {
            Gpio gpio = new Gpio(new ShellAction(), number, direction, edge);
            gpio.setCallback(new Gpio.Callback() {
                @Override
                public void onChange(int value) {
                    println("回调", value);
                }

                @Override
                public void onError() {
                    runOnUiThread(() -> {
                        TT.show("GPIO Error!");
                        close();
                    });
                }
            });
            gpio.open();
            this.gpio = gpio;

            mViewBinding.etNumber.setEnabled(false);
            mViewBinding.spDirection.setEnabled(false);
            mViewBinding.spEdge.setEnabled(false);

            mViewBinding.btnHigh.setEnabled(true);
            mViewBinding.btnLow.setEnabled(true);
            mViewBinding.btnGet.setEnabled(true);

            mViewBinding.btnOpen.setText("关闭");
        } catch (IOException e) {
            e.printStackTrace();
            TT.show("打开端口失败");
        }
    }

    private void close() {
        if (gpio != null) gpio.safeClose();
        gpio = null;

        mViewBinding.etNumber.setEnabled(true);
        mViewBinding.spDirection.setEnabled(true);
        mViewBinding.spEdge.setEnabled(true);

        mViewBinding.btnHigh.setEnabled(false);
        mViewBinding.btnLow.setEnabled(false);
        mViewBinding.btnGet.setEnabled(false);

        mViewBinding.btnOpen.setText("打开");
    }

    private void println(String action, int value) {
        mViewBinding.etLogs.post(new Runnable() {
            @Override
            public void run() {
                mViewBinding.etLogs.append(String.format(Locale.getDefault(), "动作：%s，电平：%s\n", action, value == 0 ? "低" : "高"));
            }
        });
    }

    private static class ShellAction implements Gpio.Action {
        @Override
        public void onExportGpio(int number) throws IOException {
            Shell.SU_C.execResultCode(
                    String.format(Locale.getDefault(), "echo %d > /sys/class/gpio/export", number)
            );
        }

        @Override
        public void onUnExportGpio(int number) throws IOException {
            Shell.SU_C.execResultCode(
                    String.format(Locale.getDefault(), "echo %d > /sys/class/gpio/unexport", number)
            );
        }

        @Override
        public void grantPermission(File file, boolean canRead, boolean canWrite, boolean canExec) {
            int mask = (file.canRead() ? 0b100 : 0) | (file.canWrite() ? 0b010 : 0) | (file.canExecute() ? 0b001 : 0);
            if (canRead && !file.canRead()) mask = mask | 0b100;
            if (canWrite && !file.canWrite()) mask = mask | 0b010;
            if (canExec && !file.canExecute()) mask = mask | 0b001;
            Shell.SU_C.execResultCode(
                    String.format(Locale.getDefault(), "chmod %d%d%d %s", mask, mask, mask, file.getAbsolutePath())
            );
        }
    }
}
