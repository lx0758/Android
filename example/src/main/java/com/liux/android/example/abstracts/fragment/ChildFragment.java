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

public class ChildFragment extends AbstractsFragment {
    private static final String TAG = "NESTING";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_abstracts_fragment_nesting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getChildFragmentManager().beginTransaction()
                .add(R.id.fl_container, new GrandsonFragment())
                .commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Chlid:onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Chlid:onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Chlid:onStop");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Chlid:onDetach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "Chlid:onHiddenChanged");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "Chlid:setUserVisibleHint");
    }
}
