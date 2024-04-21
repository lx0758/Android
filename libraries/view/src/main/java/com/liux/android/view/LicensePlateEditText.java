package com.liux.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.Arrays;

public class LicensePlateEditText extends AppCompatEditText {
    private static final int LENGTH = 8;
    private static final String ENERGY = "能源";

    private int mInterval = 0;
    private int mOneWidth = 0;

    private Paint mPaint = null;
    private Paint mPaintNew = null;
    private RectF mRectF = null;
    private Drawable mDrawableNormal = null;
    private Drawable mDrawablePressed = null;
    private Drawable mDrawableEnergy = null;

    public LicensePlateEditText(Context context) {
        super(context);
        initView(null);
    }

    public LicensePlateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public LicensePlateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left - getPaddingLeft() - getPaddingRight();
        mOneWidth = (width - (mInterval * (LENGTH - 1))) / LENGTH;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int length = length();

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float baseLine = (fontMetrics.ascent + fontMetrics.descent) / 2;

        Paint.FontMetrics fontMetricsNew = mPaint.getFontMetrics();
        float baseLineNew = (fontMetricsNew.ascent + fontMetricsNew.descent) / 2;

        int left = getPaddingLeft();
        for (int i = 0; i < LENGTH; i++) {
            mRectF.set(left, 0, left + mOneWidth, height);

            // 画边框
            if (i == length) {
                if (mDrawablePressed != null) {
                    mDrawablePressed.setBounds((int) mRectF.left, (int) mRectF.top, (int) mRectF.right, (int) mRectF.bottom);
                    mDrawablePressed.draw(canvas);
                }
            } else if (i == LENGTH - 1 && length == LENGTH) {
                if (mDrawablePressed != null) {
                    mDrawablePressed.setBounds((int) mRectF.left, (int) mRectF.top, (int) mRectF.right, (int) mRectF.bottom);
                    mDrawablePressed.draw(canvas);
                }
            } else if (i == LENGTH - 1 && length < LENGTH && mDrawableEnergy != null) {
                mDrawableEnergy.setBounds((int) mRectF.left, (int) mRectF.top, (int) mRectF.right, (int) mRectF.bottom);
                mDrawableEnergy.draw(canvas);
            } else {
                if (mDrawableNormal != null) {
                    mDrawableNormal.setBounds((int) mRectF.left, (int) mRectF.top, (int) mRectF.right, (int) mRectF.bottom);
                    mDrawableNormal.draw(canvas);
                }
            }

            // 画字
            if (i < length) {
                canvas.drawText(
                        getText().subSequence(i, i + 1),
                        0,
                        1,
                        mRectF.centerX(),
                        mRectF.centerY() - baseLine,
                        mPaint
                );
            }

            // 画新能源
            if (i == LENGTH - 1 && length < LENGTH) {
                canvas.drawText(
                        ENERGY,
                        0,
                        ENERGY.length(),
                        mRectF.centerX(),
                        mRectF.centerY() - baseLineNew,
                        mPaintNew
                );
            }

            left += mOneWidth + mInterval;
        }
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        //return super.getDefaultMovementMethod();
        return null;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        setSelection(getText().length());
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            setMaxLength(LENGTH);
            //LENGTH = attrs.getAttributeIntValue(android.R.attr.maxLength, LENGTH);

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LicensePlateEditText);

            mInterval = typedArray.getDimensionPixelSize(R.styleable.LicensePlateEditText_lpet_interval, mInterval);
            mDrawableNormal = typedArray.getDrawable(R.styleable.LicensePlateEditText_lpet_drawable_normal);
            mDrawablePressed = typedArray.getDrawable(R.styleable.LicensePlateEditText_lpet_drawable_pressed);
            mDrawableEnergy = typedArray.getDrawable(R.styleable.LicensePlateEditText_lpet_drawable_energy);

            typedArray.recycle();
        }

        if (mInterval < 0) mInterval = 0;

        mPaint = getPaint();
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaintNew = new TextPaint(mPaint);
        mPaintNew.setColor(getCurrentHintTextColor());
        mPaintNew.setTextAlign(Paint.Align.CENTER);
        mRectF = new RectF();

        setShieldSelector();
        setMaxLength(LENGTH);
        setBackgroundColor(Color.TRANSPARENT);
        setInputType(EditorInfo.TYPE_CLASS_TEXT);
    }

    private void setMaxLength(int maxLength) {
        InputFilter[] oldInputFilters = getFilters();
        InputFilter[] newInputFilters = Arrays.copyOf(oldInputFilters, oldInputFilters.length + 1);
        newInputFilters[oldInputFilters.length] = new InputFilter.LengthFilter(maxLength);
        setFilters(newInputFilters);
    }

    private void setShieldSelector() {
        setLongClickable(false);
        setCursorVisible(false);
        setCustomSelectionActionModeCallback(new ActionMode.Callback(){
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }
}
