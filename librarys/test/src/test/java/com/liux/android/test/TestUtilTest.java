package com.liux.android.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestUtilTest extends RobolectricTest {

    @Test
    public void base() {
        assertNotNull(getApplication());
        assertNotNull(getContext());

        System.out.println(getContext().getPackageName());
        System.out.println(getContext().getApplicationInfo().packageName);
    }
}