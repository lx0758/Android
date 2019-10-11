package com.liux.android.example.abstracts.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.liux.android.abstracts.AbstractsActivity;
import com.liux.android.example.R;
import com.liux.android.example.abstracts.fragment.ChildFragment;

public class NestingFragmentActivity extends AbstractsActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstracts_fragment_nesting);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_container, new ChildFragment())
                .commit();
    }
}
