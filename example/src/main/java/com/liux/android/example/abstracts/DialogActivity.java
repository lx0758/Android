package com.liux.android.example.abstracts;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.View;

import com.liux.android.abstracts.AbstractsDialog;
import com.liux.android.example.R;

/**
 * Created by Liux on 2017/12/3.
 */

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_dialog);
    }

    public void onClick(View view) {
        Dialog dialog;
        switch (view.getId()) {
            case R.id.btn_dialog:
                dialog = new Dialog(this);
                dialog.setTitle("标题");
                dialog.setContentView(R.layout.activity_abstracts);
                dialog.show();
                break;
            case R.id.btn_app_dialog:
                dialog = new AppCompatDialog(this);
                dialog.setTitle("标题");
                dialog.setContentView(R.layout.activity_abstracts);
                dialog.show();
                break;
            case R.id.btn_base_dialog:
                dialog = new AbstractsDialog(this) {}.setFullScreen(true).setBackgroundColor(Color.YELLOW);
                dialog.setTitle("标题");
                dialog.setContentView(R.layout.activity_abstracts);
                dialog.show();
                break;
        }
    }
}
