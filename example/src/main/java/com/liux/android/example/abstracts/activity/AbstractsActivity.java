package com.liux.android.example.abstracts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.R;

/**
 * Created by Liux on 2017/11/28.
 */

public class AbstractsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstracts);
    }

    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_activity_transparent:
                intent = new Intent(this, TransparentTitleBarActivity.class);
                break;
            case R.id.btn_activity_default:
                intent = new Intent(this, DefaultTitleBarActivity.class);
                break;
            case R.id.btn_activity_white:
                intent = new Intent(this, WhiteTitleBarActivity.class);
                break;
            case R.id.btn_activity_no:
                intent = new Intent(this, NoTitleBarActivity.class);
                break;
            case R.id.btn_fragment:
                intent = new Intent(this, FragmentActivity.class);
                break;
            case R.id.btn_fragment_nesting:
                intent = new Intent(this, NestingFragmentActivity.class);
                break;
        }
        startActivity(intent);
    }
}
