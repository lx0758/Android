package com.liux.android.mediaer;

public class MediaerException extends Exception {

    /* 取消了操作 */
    public static final int TYPE_CANCEL = -1;
    /* 其他错误权限 */
    public static final int TYPE_UNKNOWN = 0;
    /* 没有找到应用程序 */
    public static final int TYPE_INTENT = 1;
    /* 没有相关权限 */
    public static final int TYPE_PERMISSION = 2;

    private int type;

    public MediaerException(int type) {
        this.type = type;
    }

    public MediaerException(int type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
