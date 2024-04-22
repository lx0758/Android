package com.liux.android.qrcode.camrea;

public class PreviewFrame {
    private byte[] yuv;
    private int width;
    private int height;

    public PreviewFrame() {
    }

    public PreviewFrame(byte[] yuv, int width, int height) {
        this.yuv = yuv;
        this.width = width;
        this.height = height;
    }

    public byte[] getYuv() {
        return yuv;
    }

    public void setYuv(byte[] yuv) {
        this.yuv = yuv;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
