package com.liux.android.test;

public class Business {

    public static void staticMethod() {
        System.out.println("Business, staticMethod");
    }

    public Business() {
        System.out.println("Business, construction");
    }

    public void method() {
        System.out.println("Business, method");
    }
}
