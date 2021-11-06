package com.liux.android.io.gpio;

import android.annotation.SuppressLint;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.liux.android.io.Shell;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

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

    // JNI 使用
    private long pollHandler;

    private Shell shell;
    private int number;
    private String direction;
    private String edge;

    private Callback callback;
    private boolean alreadyOpen = false;
    private boolean needUnExport = false;

    @SuppressLint("WrongConstant")
    public Gpio(Shell shell, int number) {
        this(shell, number, DIRECTION_OUT, EDGE_NONE);
    }

    public Gpio(Shell shell, int number, @DIRECTION String direction, @EDGE String edge) {
        this.shell = shell;
        this.number = number;
        this.direction = direction;
        this.edge = edge;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void open() throws SecurityException, IOException {
        close();
        File operate;
        // 导出引脚
        operate = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/value", number));
        if (!operate.exists()) {
            if (shell.execResultCodeBySu(String.format(Locale.CHINA, "echo %d > /sys/class/gpio/export", number)) != 0) {
                throw new IOException("Export gpio port " + number + " failure");
            }
            needUnExport = true;
        }
        if (!operate.exists()) {
            throw new IOException("Export gpio port " + number + " failure");
        }
        checkAndChangePermission(operate, true, true, false);
        // 设置方向
        operate = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/direction", number));
        checkAndChangePermission(operate, true, true, false);
        if (shell.execResultCodeBySu(String.format("echo %s > %s", direction, operate.getAbsolutePath())) != 0) {
            throw new IOException("Set gpio port " + number + " direction failure");
        }
        // 设置中断
        operate = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/edge", number));
        checkAndChangePermission(operate, true, true, false);
        if (DIRECTION_IN.equals(direction)) {
            if (shell.execResultCodeBySu(String.format("echo %s > %s", edge, operate.getAbsolutePath())) != 0) {
                throw new IOException("Set gpio port " + number + " edge failure");
            }
        } else {
            if (shell.execResultCodeBySu(String.format("echo %s > %s", EDGE_NONE, operate.getAbsolutePath())) != 0) {
                throw new IOException("Set gpio port " + number + " edge failure");
            }
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

    public void close() {
        // 停止监听
        if (DIRECTION_IN.equals(direction)) {
            jniPollStop();
        }
        // 清理导出
        if (needUnExport) {
            shell.execResultCodeBySu(String.format(Locale.CHINA, "echo %d > /sys/class/gpio/unexport", number));
        }
        // 清理状态
        alreadyOpen = false;
    }

    public boolean set(@VALUE int value) throws SecurityException {
        if (!isOpen()) return false;
        if (!DIRECTION_OUT.equals(direction)) return false;
        File valueFile = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/value", number));
        if (!valueFile.canWrite()) throw new SecurityException();
        return shell.execResultCodeBySu(String.format(Locale.CHINA, "echo %d > %s", value, valueFile.getAbsolutePath())) == 0;
    }

    public @VALUE int get() throws SecurityException {
        if (!isOpen()) return VALUE_LOW;
        File valueFile = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/value", number));
        if (!valueFile.canRead()) throw new SecurityException();
        String result = shell.execResultStringBySu(String.format("cat %s", valueFile.getAbsolutePath()));
        if (result == null) return VALUE_LOW;
        return Integer.parseInt(result);
    }

    private void checkAndChangePermission(File file, boolean canRead, boolean canWrite, boolean canExec) throws SecurityException {
        int mask = (file.canRead() ? 0b100 : 0) | (file.canWrite() ? 0b010 : 0) | (file.canExecute() ? 0b001 : 0);
        if (canRead && !file.canRead()) mask = mask | 0b100;
        if (canWrite && !file.canWrite()) mask = mask | 0b010;
        if (canExec && !file.canExecute()) mask = mask | 0b001;
        try {
            /* Missing read/write permission, trying to chmod the file */
            String cmd = String.format(Locale.CHINA, "chmod %d%d%d %s", mask, mask, mask, file.getAbsolutePath());
            if (shell.execResultCodeBySu(cmd) != 0) throw new IOException("change permission fail");
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    private native void jniPollStart();
    private native void jniPollStop();
    public void jniPollCallback(int type, int value) {
        if (type == TYPE_ERROR) close();
        if (callback != null) callback.onEvent(type, value);
    }
    static {
        System.loadLibrary("io-gpio");
    }

    public interface Callback {

        /**
         * GPIO 电平改变时触发
         * @param type
         * @param value
         */
        void onEvent(@TYPE int type, @VALUE int value);
    }

    @StringDef({DIRECTION_IN, DIRECTION_OUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DIRECTION {}

    @StringDef({EDGE_RISING, EDGE_FALLING, EDGE_BOTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EDGE {}

    @IntDef({TYPE_ERROR, TYPE_POLL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {}

    @IntDef({VALUE_HIGH, VALUE_LOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VALUE {}
}
