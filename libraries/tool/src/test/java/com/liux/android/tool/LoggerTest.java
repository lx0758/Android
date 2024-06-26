package com.liux.android.tool;

import com.liux.android.test.RobolectricTest;

import org.junit.Test;

public class LoggerTest extends RobolectricTest {

    private static final String TAG = "LoggerTest";

    @Test
    public void logNone() {
        Logger.setLevel(Logger.LEVEL_NONE);
        Logger.v(TAG, "Test VERBOSE", new RuntimeException());
        Logger.d(TAG, "Test DEBUG", new RuntimeException());
        Logger.i(TAG, "Test INFO", new RuntimeException());
        Logger.w(TAG, "Test WARN", new RuntimeException());
        Logger.e(TAG, "Test ERROR", new RuntimeException());
        Logger.a(TAG, "Test ASSERT", new RuntimeException());
    }

    @Test
    public void logBasic() {
        Logger.setLevel(Logger.LEVEL_BASIC);
        Logger.v(TAG, "Test VERBOSE", new RuntimeException());
        Logger.d(TAG, "Test DEBUG", new RuntimeException());
        Logger.i(TAG, "Test INFO", new RuntimeException());
        Logger.w(TAG, "Test WARN", new RuntimeException());
        Logger.e(TAG, "Test ERROR", new RuntimeException());
        Logger.a(TAG, "Test ASSERT", new RuntimeException());
    }

    @Test
    public void logDetail() {
        Logger.setLevel(Logger.LEVEL_DETAIL);
        Logger.v(TAG, "Test VERBOSE", new RuntimeException());
        Logger.d(TAG, "Test DEBUG", new RuntimeException());
        Logger.i(TAG, "Test INFO", new RuntimeException());
        Logger.w(TAG, "Test WARN", new RuntimeException());
        Logger.e(TAG, "Test ERROR", new RuntimeException());
        Logger.a(TAG, "Test ASSERT", new RuntimeException());
    }
}