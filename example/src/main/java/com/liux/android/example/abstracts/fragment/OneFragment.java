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
import com.liux.android.example.databinding.FragmentBaseOneBinding;
import com.liux.android.tool.TT;

/**
 * Created by Liux on 2017/12/3.
 */

public class OneFragment extends AbstractsFragment {

    private FragmentBaseOneBinding mViewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_one, container, false);

        mViewBinding = FragmentBaseOneBinding.bind(rootView);

        addIgnoreView(mViewBinding.btnButton1);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewBinding.btnButton1.setOnClickListener(view1 -> {
            TT.show("点击了按钮1");
        });
        mViewBinding.btnButton2.setOnClickListener(view1 -> {
            TT.show("点击了按钮2");
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FG", "One:onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("FG", "One:onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("FG", "One:onStop");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("FG", "One:onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FG", "One:onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("FG", "One:onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("FG", "One:onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("FG", "One:onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("FG", "One:onDetach");
    }
}
