package com.liux.android.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.annotation.Config;

@Config(shadows = ShadowBusiness.class)
@PowerMockIgnore({"com.liux.android.test.*"})
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
}