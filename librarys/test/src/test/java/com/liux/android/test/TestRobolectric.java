package com.liux.android.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestRobolectric extends RobolectricTest {

    @Test
    public void test() {
        assertNotNull(getApplication());
        assertNotNull(getContext());

        System.out.println(getContext().getPackageName());
        System.out.println(getContext().getApplicationInfo().packageName);
    }
}