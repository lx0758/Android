package com.liux.android.test;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(Business.class)
public class ShadowBusiness {

    @Implementation
    public static void staticMethod() {
        System.out.println("ShadowBusiness, staticMethod");
    }

    @Implementation
    public void __constructor__() {
        System.out.println("ShadowBusiness, construction");
    }

    @Implementation
    public void method() {
        System.out.println("ShadowBusiness, method");
    }
}
