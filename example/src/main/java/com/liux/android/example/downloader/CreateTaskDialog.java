package com.liux.android.example.downloader;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.liux.android.example.databinding.DialogTaskCreateBinding;
import com.liux.android.tool.TT;

public class CreateTaskDialog extends Dialog {

    private DialogTaskCreateBinding mViewBinding;

    private OnFinishListener onFinishListener;

    public CreateTaskDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = DialogTaskCreateBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mViewBinding.getRoot());

        mViewBinding.btnFinish.setOnClickListener(view -> {
            String url = mViewBinding.etUrl.getText().toString().replace("\n", "").replace("\n", "");
            String method = mViewBinding.spMethod.getSelectedItem().toString();
            String name = mViewBinding.etName.getText().toString();

            if (TextUtils.isEmpty(url)) {
                TT.show("下载地址不能为空");
                return;
            }
            if (TextUtils.isEmpty(method)) {
                TT.show("请求方法不能为空");
                return;
            }

            onFinishListener.onFinish(url, method, name);
            dismiss();
        });
    }

    public CreateTaskDialog lintener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
        return this;
    }

    public interface OnFinishListener {

        void onFinish(String url, String method, String fileName);
    }
}
