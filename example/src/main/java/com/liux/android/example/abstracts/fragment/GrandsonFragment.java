package com.liux.android.example.abstracts.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liux.android.abstracts.AbstractsFragment;
import com.liux.android.example.R;

public class GrandsonFragment extends AbstractsFragment {
    private static final String TAG = "NESTING";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_one, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Grandson:onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Grandson:onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Grandson:onStop");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Grandson:onDetach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "Grandson:onHiddenChanged");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "Grandson:setUserVisibleHint");
    }

    @Override
    public void onLazyLoad() {
        super.onLazyLoad();
        Log.d(TAG, "Grandson:onLazyLoad");
    }

    @Override
    public void onVisibleChanged() {
        super.onVisibleChanged();
        Log.d(TAG, "Grandson:onVisibleChanged");
    }
}
