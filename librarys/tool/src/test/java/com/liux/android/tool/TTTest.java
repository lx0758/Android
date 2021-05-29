package com.liux.android.tool;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TTTest {

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.systemContext;
        TT.setContext(context);
    }

    @Test
    public void testShow() {
        TT.show("TEST");
        assertEquals("TEST", ShadowToast.getTextOfLatestToast());
    }
}