package com.liux.android.tool;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class LoggerTest {

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Log.println(anyInt(), anyString(), anyString())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                System.out.printf(
                        Locale.getDefault(),
                        "priority:%d tag:%s msg:%s%n",
                        invocation.getArgument(0, Integer.class),
                        invocation.getArgument(1),
                        invocation.getArgument(2)
                );
                return 0;
            }
        });
    }

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