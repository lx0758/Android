package com.liux.android.tool;

import com.liux.android.test.RobolectricTest;

import org.junit.Test;

public class LoggerTest extends RobolectricTest {

    @Test
    public void log() {
        Logger.v("Test VERBOSE", new RuntimeException());
        Logger.d("Test DEBUG", new RuntimeException());
        Logger.i("Test INFO", new RuntimeException());
        Logger.w("Test WARN", new RuntimeException());
        Logger.e("Test ERROR", new RuntimeException());
        Logger.a("Test ASSERT", new RuntimeException());
    }
}