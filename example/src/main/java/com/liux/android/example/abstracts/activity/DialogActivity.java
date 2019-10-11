package com.liux.android.example.abstracts.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.android.abstracts.AbstractsDialog;
import com.liux.android.abstracts.AbstractsDialogFragment;
import com.liux.android.example.R;

/**
 * Created by Liux on 2017/12/3.
 */

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstracts_dialog);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_appcompat_dialog:
                AppCompatDialog appCompatDialog = new AppCompatDialog(this) {};
                appCompatDialog.setContentView(R.layout.activity_abstracts_dialog_test);
                appCompatDialog.show();
                break;
            case R.id.btn_abstracts_dialog:
                AbstractsDialog abstractsDialog = new AbstractsDialog(this) {};
                abstractsDialog.setMatchParentLayout(true, true);
                abstractsDialog.setContentView(R.layout.activity_abstracts_dialog_test);
                abstractsDialog.show();
                break;
            case R.id.btn_abstracts_dialog_fragment:
                new CustomizeDialogFragment().show(getSupportFragmentManager(), "");
                break;
        }
    }

    public static class CustomizeDialogFragment extends AbstractsDialogFragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            setMatchParentLayout(true, true);
            return inflater.inflate(R.layout.activity_abstracts_dialog_test, container, false);
        }
    }
}
