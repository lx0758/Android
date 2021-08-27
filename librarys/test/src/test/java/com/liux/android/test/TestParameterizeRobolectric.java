package com.liux.android.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.robolectric.ParameterizedRobolectricTestRunner;

import java.util.Arrays;
import java.util.Collection;

public class TestParameterizeRobolectric extends MavenParameterizedRobolectricTest {

    @ParameterizedRobolectricTestRunner.Parameters(name = "index:{index} value:[{0},{1}]")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{"string1", 1},
                new Object[]{"string2", 2},
                new Object[]{"string3", 3},
                new Object[]{"string4", 4},
                new Object[]{"string5", 5}
        );
    }

    private Object mValue1;
    private Object mValue2;

    public TestParameterizeRobolectric(Object value1, Object value2) {
        mValue1 = value1;
        mValue2 = value2;
    }

    @Test
    public void test() {
        System.out.println("value1:" + mValue1);
        System.out.println("value2:" + mValue2);
        assertNotEquals(mValue1, mValue2);
    }
}