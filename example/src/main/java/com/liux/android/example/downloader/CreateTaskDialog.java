package com.liux.android.example.downloader;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.liux.android.example.R;
import com.liux.android.tool.TT;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTaskDialog extends Dialog {

    @BindView(R.id.et_url)
    EditText etUrl;
    @BindView(R.id.sp_method)
    Spinner spMethod;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.btn_finish)
    Button btnFinish;

    private OnFinishListener onFinishListener;

    public CreateTaskDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_task_create);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_finish)
    public void onViewClicked() {
        String url = etUrl.getText().toString().replace("\n", "").replace("\n", "");
        String method = spMethod.getSelectedItem().toString();
        String name = etName.getText().toString();

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
    }

    public CreateTaskDialog lintener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
        return this;
    }

    public interface OnFinishListener {

        void onFinish(String url, String method, String fileName);
    }
}
