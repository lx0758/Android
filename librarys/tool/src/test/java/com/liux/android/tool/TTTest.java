package com.liux.android.tool;

import static org.junit.Assert.*;

import com.liux.android.test.RobolectricTest;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowToast;

public class TTTest extends RobolectricTest {

    @Before
    public void setUp() {
        TT.setContext(getContext());
    }

    @Test
    public void testShow() {
        TT.show("TEST");
        assertEquals("TEST", ShadowToast.getTextOfLatestToast());
    }
}