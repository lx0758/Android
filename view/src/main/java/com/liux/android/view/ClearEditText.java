package com.liux.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class ClearEditText extends EditText {
    private static String TAG = "ClearEditText";

    private static final int DRAWABLE_LEFT = 0;
    private static final int DRAWABLE_TOP = 1;
    private static final int DRAWABLE_RIGHT = 2;
    private static final int DRAWABLE_BOTTOM = 3;

    private int mPadding;
    private Drawable mClear;
    private boolean mCancel;
    private boolean mTouchState;

    private DrawableRightListener mRightListener = new DrawableRightListener() {
        @Override
        public void onDrawableRightClick(View view) {
            ClearEditText.this.setText(null);
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                setCompoundDrawablePadding(0);
                setCompoundDrawables(null, null, null, null);
            } else {
                setCompoundDrawablePadding(mPadding);
                setCompoundDrawables(null, null, mClear, null);
            }
        }
    };

    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && getText().length() != 0) {
                setCompoundDrawablePadding(mPadding);
                setCompoundDrawables(null, null, mClear, null);
            } else {
                setCompoundDrawablePadding(0);
                setCompoundDrawables(null, null, null, null);
            }
        }
    };

    public ClearEditText(Context context) {
        super(context);
        initView();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(attrs, 0);
        initView();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setDrawableRightListener(DrawableRightListener listener) {
        this.mRightListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT];
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                    mTouchState = true;
                    return true ;
                } else {
                    mTouchState = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchState) {
                    if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                        return true;
                    } else {
                        mTouchState = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState) {
                    if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                        if (mRightListener != null) {
                            mRightListener.onDrawableRightClick(this);
                        }
                        return true ;
                    } else {
                        mTouchState = false;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void initAttribute(AttributeSet attrs, int defStyleAttr) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.ClearEditText, defStyleAttr, 0);

        mClear = array.getDrawable(R.styleable.ClearEditText_cet_clear);
        mCancel = array.getBoolean(R.styleable.ClearEditText_cet_cancel, true);

        array.recycle();
    }

    private void initView() {
        if (mCancel) {
            mPadding = dp2px(8);
            if (mClear == null) {
                mClear = getResources().getDrawable(R.drawable.view_clearedittext_clear);
            }
            mClear.setBounds(0, 0, (int) getTextSize(), (int) getTextSize());
            addTextChangedListener(mTextWatcher);
            setOnFocusChangeListener(mOnFocusChangeListener);
        }
        setCompoundDrawablePadding(0);
        setCompoundDrawables(null, null, null, null);
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

    public interface DrawableRightListener {

        void onDrawableRightClick(View view) ;
    }
}