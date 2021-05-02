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

public class FourFragment extends AbstractsFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_four, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FG", "Four:onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("FG", "Four:onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("FG", "Four:onStop");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("FG", "Four:onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FG", "Four:onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("FG", "Four:onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("FG", "Four:onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("FG", "Four:onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("FG", "Four:onDetach");
    }
}
