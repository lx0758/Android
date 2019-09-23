package com.liux.android.boxing;

import android.app.Activity;
import android.app.Fragment;

import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;

/**
 * Created by Liux on 2017/11/13.
 */

public class Boxinger {

    public static final String TAG = "Boxinger";

    public static void init() {
        BoxingCrop.getInstance().init(new BoxingUcrop());
        BoxingMediaLoader.getInstance().init(new BoxingGlideLoader());
    }

    public static Prepare with(Activity activity) {
        return new Prepare(activity);
    }

    public static Prepare with(Fragment fragment) {
        return new Prepare(fragment.getActivity());
    }

    public static Prepare with(android.support.v4.app.Fragment fragment) {
        return new Prepare(fragment.getActivity());
    }
}
