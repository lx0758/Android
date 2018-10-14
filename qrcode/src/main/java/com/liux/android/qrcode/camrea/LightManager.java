package com.liux.android.qrcode.camrea;

public interface LightManager {

    boolean canOpenLight();

    boolean isOpenLight();

    void openLight();

    void closeLight();
}
