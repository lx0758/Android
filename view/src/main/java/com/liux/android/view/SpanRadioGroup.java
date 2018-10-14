package com.liux.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.lang.reflect.Field;

/**
 * 支持控制子孙层 RadioButton 的 RadioGroup
 * Created by Liux on 2016/11/28.
 */
public class SpanRadioGroup extends RadioGroup {
    private static String TAG = "SpanRadioGroup";

    private OnHierarchyChangeListener mRootPassThroughListener;

    public SpanRadioGroup(Context context) {
        super(context);
    }

    public SpanRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        getPassThroughListener();

        if (mRootPassThroughListener != null) {
            refreshChildRadioButton();
        }
    }

    /**
     * 刷新子孙控件寻找RadioButton
     */
    public void refreshChildRadioButton() {
        refreshChildRadioButton(this);
    }

    /**
     * 反射拿到PassThroughHierarchyChangeListener
     */
    private void getPassThroughListener() {
        try {
            Field field = RadioGroup.class.getDeclaredField("mPassThroughListener");
            field.setAccessible(true);
            Object object = field.get(this);
            mRootPassThroughListener = (OnHierarchyChangeListener) object;
        } catch (Exception e) {

        }
    }

    /**
     * 遍历子View寻找RadioButton
     * @param view
     */
    private void refreshChildRadioButton(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof RadioGroup) {
                    continue;
                } else if (child instanceof RadioButton) {
                    mRootPassThroughListener.onChildViewAdded(this, child);
                } else if (child instanceof ViewGroup) {
                    refreshChildRadioButton(child);
                }
            }
        }
    }
}
