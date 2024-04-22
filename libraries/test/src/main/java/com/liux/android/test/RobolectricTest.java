package com.liux.android.test;

import android.app.Application;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

@RunWith(RobolectricTestRunner.class)
public abstract class RobolectricTest {

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