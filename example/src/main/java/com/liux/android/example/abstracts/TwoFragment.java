package com.liux.android.example.abstracts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.android.abstracts.AbstractsFragment;
import com.liux.android.example.R;

/**
 * Created by Liux on 2017/12/3.
 */

public class TwoFragment extends AbstractsFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_two, container, false);
    }
}
