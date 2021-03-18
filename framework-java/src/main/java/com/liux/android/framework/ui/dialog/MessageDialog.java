package com.liux.android.framework.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 2017/11/11
 * By Liux
 * lx0758@qq.com
 */

public class MessageDialog extends BaseDialog<MessageDialog> {

    private TextView mTextView;

    private OnMessageListener mOnMessageListener;

    public MessageDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView(ViewGroup viewGroup) {
        mTextView = new TextView(getContext());
        mTextView.setTextColor(Color.parseColor("#333333"));
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.0F);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER;
        viewGroup.addView(mTextView, layoutParams);
    }

    @Override
    protected boolean onLeft() {
        if (mOnMessageListener != null) {
            return mOnMessageListener.onLeft();
        }
        return true;
    }

    @Override
    protected boolean onRight() {
        if (mOnMessageListener != null) {
            return mOnMessageListener.onRight();
        }
        return true;
    }

    public MessageDialog setMessage(int msgid) {
        mTextView.setText(msgid);
        return this;
    }

    public MessageDialog setMessage(CharSequence message) {
        mTextView.setText(message);
        return this;
    }

    public MessageDialog setOnMessageListener(OnMessageListener listener) {
        mOnMessageListener = listener;
        return this;
    }

    public interface OnMessageListener {

        boolean onLeft();

        boolean onRight();
    }
}
