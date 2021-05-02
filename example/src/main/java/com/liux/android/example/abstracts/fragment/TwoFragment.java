package com.liux.android.example.abstracts.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FG", "Two:onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("FG", "Two:onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("FG", "Two:onStop");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("FG", "Two:onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FG", "Two:onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("FG", "Two:onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("FG", "Two:onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("FG", "Two:onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("FG", "Two:onDetach");
    }
}
