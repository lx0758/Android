package com.liux.android.framework.other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

public class NoneViewBinding implements ViewBinding {

    private View rootView;

    public NoneViewBinding(View rootView) {
        this.rootView = rootView;
    }

    @NonNull
    @Override
    public View getRoot() {
        return rootView;
    }

    @NonNull
    public static NoneViewBinding inflate(@NonNull LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    @NonNull
    public static NoneViewBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToParent) {
        View root = new Space(inflater.getContext());
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    @NonNull
    public static NoneViewBinding bind(@NonNull View rootView) {
        return new NoneViewBinding(rootView);
    }
}