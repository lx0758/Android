package com.liux.android.permission;

import android.app.Activity;
import android.app.Fragment;

import com.liux.android.permission.floats.FloatRequest;
import com.liux.android.permission.runtime.RuntimeRequest;

public class Prepare {

    private Activity target;

    Prepare(Activity activity) {
        target = activity;
    }

    Prepare(Fragment fragment) {
        target = fragment.getActivity();
    }

    Prepare(android.support.v4.app.Fragment fragment) {
        target = fragment.getActivity();
    }

    public RuntimeRequest requestRuntime(String... permissions) {
        return new RuntimeRequest(target, permissions);
    }

    public FloatRequest requestFloat() {
        return new FloatRequest(target);
    }
}
