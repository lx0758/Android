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
import android.widget.EditText;

import java.util.Arrays;

/**
 * date：2018/03/16
 * author：Liux
 * email：lx0758@qq.com
 * description：输入类似支付PIN密码的密码输入框
 */

@SuppressLint("AppCompatCustomView")
public class PasswordInputView extends EditText {
    
    private int mLength = 6;
    private int mColor = 0xff000000;
    private int mWidth = 10;
    private int mInterval = 10;
    private Drawable mDrawable = null;

    private int mOneWidth;
    private Paint mPasswordPaint;

    private OnPasswordListener mOnPasswordListener;

    public PasswordInputView(Context context) {
        super(context);
        initView(null);
    }
    
    public PasswordInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public PasswordInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        //return super.getDefaultMovementMethod();
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(MeasureSpec.UNSPECIFIED, widthMeasureSpec), getDefaultSize(MeasureSpec.UNSPECIFIED, heightMeasureSpec));

        int width = getMeasuredWidth();
        int height = (width - ((mLength - 1) * mInterval)) / mLength;
        mOneWidth = height;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int length = length();

        int left = 0;
        for (int i = 0; i < mLength; i++) {
            RectF rect = new RectF(left, 0, left + mOneWidth, height);

            // 画边框
            if (mDrawable != null) {
                mDrawable.setBounds((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
                mDrawable.draw(canvas);
            }

            // 画圆圈
            if (i < length) {
                canvas.drawCircle(rect.left + rect.width() / 2, rect.top + rect.height() / 2, mWidth, mPasswordPaint);
            }

            left += mOneWidth + mInterval;
        }
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        setSelection(getText().length());
    }
    
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        invalidate();

        if (mOnPasswordListener != null) {
            mOnPasswordListener.onPasswordChange(text);
        }

        if (text.length() >= mLength) {
            if (mOnPasswordListener != null) {
                mOnPasswordListener.onPasswordFinished(text.subSequence(0, mLength));
            }
        }
    }

    public void setOnPasswordListener(OnPasswordListener listener) {
        this.mOnPasswordListener = listener;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param dpValue
     * @return
     */
    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            mLength = attrs.getAttributeIntValue(android.R.attr.maxLength, mLength);

            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PasswordInputView);

            mWidth = ta.getDimensionPixelSize(R.styleable.PasswordInputView_piv_size, dp2px(mWidth));
            mColor = ta.getColor(R.styleable.PasswordInputView_piv_color, mColor);
            mDrawable = ta.getDrawable(R.styleable.PasswordInputView_piv_drawable);
            mInterval = ta.getDimensionPixelSize(R.styleable.PasswordInputView_piv_interval, dp2px(mInterval));

            ta.recycle();
        }

        if (mLength < 1) mLength = 6;
        if (mInterval < 0) mInterval = 0;

        setBackgroundColor(Color.TRANSPARENT);
        setMaxLength(mLength);
        setShieldSelector();

        mPasswordPaint = new Paint();
        mPasswordPaint.setAntiAlias(true);
        mPasswordPaint.setColor(mColor);
        mPasswordPaint.setStrokeWidth(mWidth);
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

    public interface OnPasswordListener {

        void onPasswordChange(CharSequence charSequence);

        void onPasswordFinished(CharSequence charSequence);
    }
}
