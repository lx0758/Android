package com.liux.android.test;

import static org.junit.Assert.*;

import android.os.Build;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;

/**
 * @see org.robolectric.plugins.DefaultSdkProvider
 */
@Config(sdk = Build.VERSION_CODES.S, shadows = ShadowBusiness.class)
public class TestRobolectric extends RobolectricTest {

    @Test
    public void test() {
        assertNotNull(getApplication());
        assertNotNull(getContext());

        System.out.println(getContext().getPackageName());
        System.out.println(getContext().getApplicationInfo().packageName);
    }

    @Test
    public void testShadow() {
        Business.staticMethod();
        new Business().method();
    }

    @Test
    public void testMock() {
        MockedStatic<Business> mockStaticBusiness = Mockito.mockStatic(Business.class);
        mockStaticBusiness.when(Business::staticMethod).then(invocation -> {
            System.out.println("MockStaticBusiness, staticMethod");
            return null;
        });
        Business.staticMethod();
        new Business().method();
    }
}