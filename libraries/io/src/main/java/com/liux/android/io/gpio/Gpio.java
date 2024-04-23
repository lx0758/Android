package com.liux.android.io.gpio;

import android.annotation.SuppressLint;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.liux.android.io.Util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/** @noinspection unused */
public class Gpio {

    public static final String DIRECTION_IN = "in";
    public static final String DIRECTION_OUT = "out";

    /* 表示引脚为输出，不是中断引脚 */
    public static final String EDGE_NONE = "none";
    /* 表示引脚为中断输入，上升沿触发 */
    public static final String EDGE_RISING = "rising";
    /* 表示引脚为中断输入，下降沿触发 */
    public static final String EDGE_FALLING = "falling";
    /* 表示引脚为中断输入，边沿触发 */
    public static final String EDGE_BOTH = "both";

    public static final int TYPE_ERROR = -1;
    public static final int TYPE_POLL = 1;

    public static final int VALUE_HIGH = 1;
    public static final int VALUE_LOW = 0;

    static {
        System.loadLibrary("io-gpio");
    }

    private final Action action;
    private final int number;
    private final String direction, edge;

    private Callback callback;
    private boolean alreadyOpen = false;
    private boolean needUnExport = false;

    // JNI 使用
    private long pollHandler;

    @SuppressLint("WrongConstant")
    public Gpio(Action action, int number) {
        this(action, number, DIRECTION_OUT, EDGE_NONE);
    }

    public Gpio(Action action, int number, @DIRECTION String direction, @EDGE String edge) {
        this.action = action;
        this.number = number;
        this.direction = direction;
        this.edge = edge;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void open() throws IOException {
        close();
        File operate;

        // 导出引脚
        operate = new File(String.format(Locale.getDefault(), "/sys/class/gpio/gpio%d/value", number));
        if (!operate.exists()) {
            if (action == null) {
                throw new IOException("Gpio port " + number + " does not exist");
            }
            action.onExportGpio(number);
            if (!operate.exists()) {
                throw new IOException("Export gpio port " + number + " failure");
            }
            needUnExport = true;
        }
        grantPermissionIfNeed(operate, true, true, false);

        // 设置方向
        operate = new File(String.format(Locale.getDefault(), "/sys/class/gpio/gpio%d/direction", number));
        grantPermissionIfNeed(operate, true, true, false);
        if (!Util.writeFile(operate, direction)) {
            throw new IOException("Set gpio port " + number + " direction failure");
        }

        // 设置中断
        operate = new File(String.format(Locale.getDefault(), "/sys/class/gpio/gpio%d/edge", number));
        grantPermissionIfNeed(operate, true, true, false);
        String finalEdge = DIRECTION_IN.equals(direction) ? edge : EDGE_NONE;
        if (!Util.writeFile(operate, finalEdge)) {
            throw new IOException("Set gpio port " + number + " edge failure");
        }

        // 设置监听
        if (DIRECTION_IN.equals(direction)) {
            jniPollStart();
        }

        // 记录状态
        alreadyOpen = true;
    }

    public int getNumber() {
        return number;
    }

    public String getDirection() {
        return direction;
    }

    public boolean isOpen() {
        return alreadyOpen;
    }

    public void close() throws IOException {
        // 停止监听
        if (DIRECTION_IN.equals(direction)) {
            jniPollStop();
        }
        // 清理导出
        if (needUnExport) {
            action.onUnExportGpio(number);
        }
        // 清理状态
        alreadyOpen = false;
    }

    public void safeClose() {
        try {
            close();
        } catch (IOException ignored) {}
    }

    public boolean set(@VALUE int value) {
        if (!isOpen()) return false;
        if (!DIRECTION_OUT.equals(direction)) return false;
        File valueFile = new File(String.format(Locale.getDefault(), "/sys/class/gpio/gpio%d/value", number));
        try {
            return Util.writeFile(valueFile, String.valueOf(value));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public @VALUE int get() {
        if (!isOpen()) return VALUE_LOW;
        File valueFile = new File(String.format(Locale.getDefault(), "/sys/class/gpio/gpio%d/value", number));
        try {
            String result = Util.readFile(valueFile);
            return Integer.parseInt(result.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return VALUE_LOW;
        }
    }

    /** @noinspection SameParameterValue*/
    private void grantPermissionIfNeed(File file, boolean canRead, boolean canWrite, boolean canExec) throws IOException {
        if (file.canRead() == canRead && file.canWrite() == canWrite && file.canExecute() == canExec) return;
        try {
            action.grantPermission(file, canRead, canWrite, canExec);
            if (file.canRead() != canRead && file.canWrite() != canWrite && file.canExecute() != canExec) {
                throw new IOException("change permission fail");
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private native void jniPollStart();
    private native void jniPollStop();
    public void jniPollCallback(int type, int value) {
        if (type == TYPE_ERROR) safeClose();
        if (callback != null) {
            switch (type) {
                case TYPE_ERROR:
                    callback.onError();
                    break;
                case TYPE_POLL:
                    callback.onChange(value);
                    break;
                default:
                    break;
            }
        }
    }

    public interface Action {

        void onExportGpio(int number) throws IOException;

        void onUnExportGpio(int number) throws IOException;

        void grantPermission(File file, boolean canRead, boolean canWrite, boolean canExec);
    }

    public interface Callback {

        /**
         * GPIO 电平改变时触发
         * @param value {@link VALUE}
         */
        void onChange(@VALUE int value);

        void onError();
    }

    @StringDef({DIRECTION_IN, DIRECTION_OUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DIRECTION {}

    @StringDef({EDGE_RISING, EDGE_FALLING, EDGE_BOTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EDGE {}

    @IntDef({VALUE_HIGH, VALUE_LOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VALUE {}
}
