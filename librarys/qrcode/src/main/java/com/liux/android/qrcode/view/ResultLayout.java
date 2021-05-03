package com.liux.android.qrcode.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.liux.android.qrcode.QRCode;
import com.liux.android.qrcode.util.SizeUtil;

import java.util.List;

public class ResultLayout extends ViewGroup {
    private static final int GRAY_TRANSPARENT = Color.parseColor("#A0000000");

    private OnResultListener mOnResultListener;

    public ResultLayout(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int size = SizeUtil.getResultSize(getContext());
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof ResultView) {
                ResultView resultView = (ResultView) child;
                QRCode qrCode = resultView.getQRCode();
                int childLeft = (int) ((right - left) * qrCode.getXRatio());
                int childTop = (int) ((bottom - top) * qrCode.getYRatio());
                int childWidth = size;
                int childHeight = size;
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            } else if (child instanceof TextView) {
                TextView textView = (TextView) child;
                int textBottom = bottom - SizeUtil.dp2px(getContext(), 80);
                int textHeight = SizeUtil.dp2px(getContext(), textView.getTextSize());
                child.layout(
                        left,
                        textBottom - textHeight,
                        right,
                        textBottom
                );
            }
        }
    }

    public void setOnSelectListener(OnResultListener onResultListener) {
        mOnResultListener = onResultListener;
    }

    public void clear() {
        setBackgroundColor(Color.TRANSPARENT);
        removeAllViews();
    }

    public void showResult(final QRCode qrCode) {
        setBackgroundColor(GRAY_TRANSPARENT);
        addShowBarcode(qrCode, null);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mOnResultListener != null) {
                    mOnResultListener.onResult(qrCode);
                }
            }
        }, 500);
    }

    public void selectResult(List<QRCode> qrCodes) {
        setBackgroundColor(GRAY_TRANSPARENT);
        for (QRCode qrCode : qrCodes) {
            addShowBarcode(qrCode, mOnResultListener);
        }
        addHintText();
    }

    private TextView mCacheTextView;
    private void addHintText() {
        if (mCacheTextView == null) {
            TextView textView = new TextView(getContext());
            textView.setText("请轻触选择一个识别对象");
            textView.setTextSize(14);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            mCacheTextView = textView;
        }
        addView(mCacheTextView);
    }

    private void addShowBarcode(final QRCode qrCode, final OnResultListener onResultListener) {
        ResultView resultView = new ResultView(getContext());
        resultView.setQRCde(qrCode);
        resultView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResultListener != null) {
                    onResultListener.onResult(qrCode);
                }
            }
        });
        addView(resultView);
    }

    public interface OnResultListener {

        void onResult(QRCode qrCode);

        void onCancel();
    }
}
