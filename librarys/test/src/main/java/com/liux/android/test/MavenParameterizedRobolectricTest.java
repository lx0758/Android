package com.liux.android.test;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

@RunWith(MavenParameterizedRobolectricTestRunner.class)
/**
 * @see org.robolectric.plugins.DefaultSdkProvider
 */
@Config(sdk = Build.VERSION_CODES.R)
@PowerMockIgnore({"org.robolectric.*", "org.powermock.*", "org.mockito.*", "android.*", "androidx.*", "org.json.*", "sun.security.*", "javax.net.*"})
public abstract class MavenParameterizedRobolectricTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

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
