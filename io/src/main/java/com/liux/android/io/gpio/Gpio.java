package com.liux.android.io.gpio;

import android.annotation.SuppressLint;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
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

    private static final int RESULT_OK = 0;

    private int gpio;
    private String direction;
    private String edge;

    private Callback callback;
    private boolean alreadyOpen = false;

    @SuppressLint("WrongConstant")
    public Gpio(int gpio, @DIRECTION String direction) {
        this(gpio, direction, EDGE_NONE);
    }

    public Gpio(int gpio, @DIRECTION String direction, @EDGE_IN String edge) {
        this.gpio = gpio;
        this.direction = direction;
        this.edge = edge;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void open() throws SecurityException, IOException {
        close();
        File operate, valueFile = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/value", this.gpio));
        // 导出引脚
        if (!valueFile.exists()) {
            operate = new File("/sys/class/gpio/export");
            checkAndChangePermission(operate, true, true, false);
            if (execToCode(String.format(Locale.CHINA, "echo %d > %s", this.gpio, operate.getAbsolutePath())) != RESULT_OK) {
                throw new IOException("Export gpio port " + gpio + " failure");
            }
        }
        if (!valueFile.exists()) {
            throw new IOException("Export gpio port " + gpio + " failure");
        }
        // 设置方向
        operate = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/direction", this.gpio));
        checkAndChangePermission(operate, true, true, false);
        if (execToCode(String.format("echo %s > %s", this.direction, operate.getAbsolutePath())) != RESULT_OK) {
            throw new IOException("Set gpio port " + gpio + " direction failure");
        }
        // 设置中断
        operate = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/edge", this.gpio));
        checkAndChangePermission(operate, true, true, false);
        if (DIRECTION_IN.equals(direction)) {
            if (execToCode(String.format("echo %s > %s", edge, operate.getAbsolutePath())) != RESULT_OK) {
                throw new IOException("Set gpio port " + gpio + " edge failure");
            }
        } else {
            if (execToCode(String.format("echo %s > %s", EDGE_NONE, operate.getAbsolutePath())) != RESULT_OK) {
                throw new IOException("Set gpio port " + gpio + " edge failure");
            }
        }
        // 设置监听
        if (DIRECTION_IN.equals(direction)) {
            checkAndChangePermission(valueFile, true, false, false);
            _startPoll(gpio);
        }
        alreadyOpen = true;
    }

    public int getGpio() {
        return gpio;
    }

    public String getDirection() {
        return direction;
    }

    public boolean isOpen() {
        return alreadyOpen;
    }

    public void close() {
        _stopPoll();
        if (new File("/sys/class/gpio/unexport").canWrite()) {
            execToCode(String.format(Locale.CHINA, "echo %d > /sys/class/gpio/unexport", this.gpio));
        }
        alreadyOpen = false;
    }

    public boolean set(@VALUE int value) throws SecurityException {
        if (!isOpen()) return false;
        if (!DIRECTION_OUT.equals(direction)) return false;
        File valueFile = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/value", gpio));
        if (!valueFile.canWrite()) throw new SecurityException();
        return execToCode(String.format(Locale.CHINA, "echo %d > %s", value, valueFile.getAbsolutePath())) == RESULT_OK;
    }

    public @VALUE int get() throws SecurityException {
        if (!isOpen()) return VALUE_LOW;
        File valueFile = new File(String.format(Locale.CHINA, "/sys/class/gpio/gpio%d/value", gpio));
        if (!valueFile.canRead()) throw new SecurityException();
        String result = execToString(String.format("cat %s", valueFile.getAbsolutePath()));
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
            Process su;
            su = Runtime.getRuntime().exec("su");
            String cmd = String.format(Locale.CHINA, "chmod %d%d%d %s && exit\n", mask, mask, mask, file.getAbsolutePath());
            su.getOutputStream().write(cmd.getBytes());
            if ((su.waitFor() != 0)) throw new SecurityException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException();
        }
    }

    private int execToCode(String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("sh");
            exec(process, cmd);
            return process.exitValue();
        } catch (IOException e) {
            e.printStackTrace();
            return process != null ? process.exitValue() : -1;
        }
    }

    private String execToString(String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("sh");
            return exec(process, cmd);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String exec(Process process, String... cmds) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();

        for (String cmd : cmds) {
            outputStream.write(cmd.getBytes());
            outputStream.write("\n".getBytes());
        }
        outputStream.write("exit".getBytes());
        outputStream.write("\n".getBytes());
        outputStream.flush();

        try {
            if (process.waitFor() != 0) throw new IOException();
        } catch (InterruptedException e) {
            return null;
        }

        String result = null;
        int length = inputStream.available();
        if (length > 0) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            if (bytes[length - 1] == '\n') bytes = Arrays.copyOf(bytes, length - 1);
            result = new String(bytes);
        }

        outputStream.close();
        inputStream.close();

        return result;
    }

    private native void _startPoll(int number);
    private native void _stopPoll();
    public void _onCallback(int type, int value) {
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
    public @interface EDGE_IN {}

    @IntDef({TYPE_ERROR, TYPE_POLL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {}

    @IntDef({VALUE_HIGH, VALUE_LOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VALUE {}
}
