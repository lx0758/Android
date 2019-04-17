package com.liux.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.util.Arrays;

/**
 * date：2018/2/25
 * author：Liux
 * email：lx0758@qq.com
 * description：验证码输入编辑框
 */

@SuppressLint("AppCompatCustomView")
public class AuthCodeEditText extends EditText {

    private int mLength = 6;
    private int mInterval = 0;
    private int mOneWidth = 0;

    private Paint mPaint = null;
    private RectF mRectF = null;
    private Drawable mDrawable = null;
    private Drawable mInputDrawable = null;
    private OnAuthCodeListener mOnAuthCodeListener = null;

    public AuthCodeEditText(Context context) {
        super(context);
        initView(null);
    }

    public AuthCodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public AuthCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left - getPaddingLeft() - getPaddingRight();
        mOneWidth = (width - (mInterval * (mLength - 1))) / mLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int length = length();

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float baseLine = (fontMetrics.ascent + fontMetrics.descent) / 2;

        int left = getPaddingLeft();
        for (int i = 0; i < mLength; i++) {
            mRectF.set(left, 0, left + mOneWidth, height);

            // 画边框
            if (mInputDrawable != null && length > i) {
                mInputDrawable.setBounds((int) mRectF.left, (int) mRectF.top, (int) mRectF.right, (int) mRectF.bottom);
                mInputDrawable.draw(canvas);
            } else if (mDrawable != null) {
                mDrawable.setBounds((int) mRectF.left, (int) mRectF.top, (int) mRectF.right, (int) mRectF.bottom);
                mDrawable.draw(canvas);
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

            left += mOneWidth + mInterval;
        }
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        //return super.getDefaultMovementMethod();
        return null;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (mOnAuthCodeListener != null) {
            mOnAuthCodeListener.onAuthCodeChange(text);
        }

        if (text.length() >= mLength) {
            if (mOnAuthCodeListener != null) {
                mOnAuthCodeListener.onAuthCodeFinished(text.subSequence(0, mLength));
            }
        }
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        setSelection(getText().length());
    }

    public void setOnAuthCodeListener(OnAuthCodeListener listener) {
        this.mOnAuthCodeListener = listener;
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            mLength = attrs.getAttributeIntValue(android.R.attr.maxLength, mLength);

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AuthCodeEditText);

            mInterval = typedArray.getDimensionPixelSize(R.styleable.AuthCodeEditText_acet_interval, mInterval);
            mDrawable = typedArray.getDrawable(R.styleable.AuthCodeEditText_acet_drawable);
            mInputDrawable = typedArray.getDrawable(R.styleable.AuthCodeEditText_acet_drawable_input);

            typedArray.recycle();
        }

        if (mLength < 1) mLength = 6;
        if (mInterval < 0) mInterval = 0;

        mPaint = getPaint();
        mRectF = new RectF();
        mPaint.setTextAlign(Paint.Align.CENTER);

        setShieldSelector();
        setMaxLength(mLength);
        setBackgroundColor(Color.TRANSPARENT);
        setInputType(EditorInfo.TYPE_CLASS_NUMBER);
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

    public interface OnAuthCodeListener {

        void onAuthCodeChange(CharSequence charSequence);

        void onAuthCodeFinished(CharSequence charSequence);
    }
}
