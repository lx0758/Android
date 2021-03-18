package com.liux.android.framework.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import com.liux.android.framework.R;

/**
 * 2017/11/6
 * By Liux
 * lx0758@qq.com
 */

public abstract class BaseDialog<T extends BaseDialog> extends AppCompatDialog {

    private View mRoot;
    private Button mLeft, mRight;
    private Space mInterval;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_left) {
                if (!onLeft()) return;
            } else {
                if (!onRight()) return;
            }
            dismiss();
        }
    };

    public BaseDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    public BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public T setLeftText(CharSequence cancel) {
        mLeft.setText(cancel);
        return (T) this;
    }

    public T setRightText(CharSequence ensure) {
        mRight.setText(ensure);
        return (T) this;
    }

    public T setLeftTextColor(int color) {
        mLeft.setTextColor(color);
        return (T) this;
    }

    public T setRightTextColor(int color) {
        mRight.setTextColor(color);
        return (T) this;
    }

    public T hideLeft() {
        mLeft.setVisibility(View.GONE);
        mInterval.setVisibility(View.GONE);
        mRight.setBackgroundResource(R.drawable.dialog_base_bg_right_all);
        return (T) this;
    }

    public T hideRight() {
        mRight.setVisibility(View.GONE);
        mInterval.setVisibility(View.GONE);
        mLeft.setBackgroundResource(R.drawable.dialog_base_bg_left_all);
        return (T) this;
    }

    protected abstract void initView(ViewGroup viewGroup);

    protected abstract boolean onLeft();

    protected abstract boolean onRight();

    private void init() {
        getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mRoot = View.inflate(getContext(), R.layout.dialog_base, null);
        mRoot.setOnClickListener(mOnClickListener);

        setContentView(mRoot);

        mLeft = findViewById(R.id.btn_left);
        mRight = findViewById(R.id.btn_right);
        mInterval = findViewById(R.id.sp_interval);
        mLeft.setOnClickListener(mOnClickListener);
        mRight.setOnClickListener(mOnClickListener);

        ViewGroup viewGroup = findViewById(R.id.fl_content);
        initView(viewGroup);
    }
}
