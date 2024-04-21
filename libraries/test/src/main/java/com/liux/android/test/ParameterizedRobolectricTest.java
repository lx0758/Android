package com.liux.android.test;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

@RunWith(ParameterizedRobolectricTestRunner.class)
public abstract class ParameterizedRobolectricTest {

    @Before
    public void setUpRobolectricTest() {
        ShadowLog.stream = System.out;
    }

    public Application getApplication() {
        return ApplicationProvider.getApplicationContext();
    }

    public Context getContext() {
        return ApplicationProvider.getApplicationContext();
    }
}
