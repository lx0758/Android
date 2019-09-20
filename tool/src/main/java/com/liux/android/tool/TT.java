package com.liux.android.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * ToastTool => TT
 * Created by Liux on 2017/11/8.
 */

public class TT {

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    private static int LAYOUT;
    private static Toast TOAST;
    private static Context CONTEXT;

    public static void setContext(Context context) {
        CONTEXT = context.getApplicationContext();
    }

    public static void setLayout(@LayoutRes int resId) {
        LAYOUT = resId;
    }

    public static void show(int resId) {
        makeText(resId, LENGTH_SHORT).show();
    }

    public static void show(CharSequence text) {
        makeText(text, LENGTH_SHORT).show();
    }

    public static Toast makeText(int resId, int duration) {
        return makeText(CONTEXT.getString(resId), duration);
    }

    @SuppressLint("ShowToast")
    public static Toast makeText(CharSequence text, int duration) {
        if (TOAST == null) {
            if (LAYOUT != 0) {
                LayoutInflater inflate = (LayoutInflater) CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflate.inflate(LAYOUT, null);

                TOAST = new Toast(CONTEXT);
                TOAST.setView(view);
                TOAST.setText(text);
                TOAST.setDuration(duration);
            } else {
                TOAST = Toast.makeText(CONTEXT, text, duration);
            }
        } else {
            TOAST.setText(text);
            TOAST.setDuration(duration);
        }
        return TOAST;
    }

    public static void cancel() {
        if (TOAST != null) {
            TOAST.cancel();
            TOAST = null;
        }
    }
}
