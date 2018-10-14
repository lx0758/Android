package com.liux.android.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by Liux on 2016/9/7.
 */
public class ClipImageLayout extends RelativeLayout {
    private String TAG = "ClipImageLayout";

    private final int MIN_MARGIN = 20;

    private Button mButton;
    private ClipImageView mClipImageView;
    private ClipBorderView mClipBorderView;

    private Uri mIn;
    private int mOutWidth, mOutHeight;

    private int mPrecinctWidth, mPrecinctHeight;
    private int mVerticalMargin, mHorizontalMargin;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mClipImageView.rotate();
        }
    };

    public ClipImageLayout(Context context) {
        super(context);
        initView();
    }

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ClipImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        computeSize();
    }

    public void initialize(Uri in, int outWidth, int outHeight) {
        mIn = in;
        mOutWidth = outWidth;
        mOutHeight = outHeight;

        computeSize();
    }

    public Bitmap clip() {
        return mClipImageView.clip();
    }

    private void initView() {
        setBackgroundColor(Color.parseColor("#000000"));

        mClipImageView = new ClipImageView(getContext());
        addView(mClipImageView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mClipBorderView = new ClipBorderView(getContext());
        addView(mClipBorderView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        /* 按钮 */
        mButton = new Button(getContext());
        mButton.setText("旋转");
        /* 字体 */
        ColorStateList color = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_pressed},
                        new int[]{-android.R.attr.state_pressed}},
                new int[]{Color.parseColor("#A0A0A0"),
                        Color.WHITE});
        mButton.setTextColor(color);
        /* 背景 */
        GradientDrawable background_pressed = new GradientDrawable();
        background_pressed.setColor(Color.parseColor("#30FFFFFF"));
        background_pressed.setCornerRadius(20);
        background_pressed.setStroke(dp2px( 1.0f), Color.parseColor("#A0A0A0"));
        GradientDrawable background_normal = new GradientDrawable();
        background_normal.setColor(Color.TRANSPARENT);
        background_normal.setCornerRadius(20);
        background_normal.setStroke(dp2px(1.0f), Color.WHITE);
        StateListDrawable background = new StateListDrawable();
        background.addState(new int[]{android.R.attr.state_pressed}, background_pressed);
        background.addState(new int[]{-android.R.attr.state_pressed}, background_normal);
        mButton.setBackgroundDrawable(background);
        /* 布局 */
        LayoutParams lp = new LayoutParams(dp2px(120.0f), dp2px(40.0f));
        lp.bottomMargin = dp2px(20.0f);
        lp.addRule(CENTER_HORIZONTAL, TRUE);
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        mButton.setLayoutParams(lp);
        mButton.setPadding(0, 0, 0, 0);
        mButton.setOnClickListener(mOnClickListener);
        addView(mButton);
    }

    private void computeSize() {
        if (getWidth() <= 0 || getHeight() <= 0) return;

        mHorizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_MARGIN, getResources().getDisplayMetrics());

        mPrecinctWidth = getMeasuredWidth() - 2 * mHorizontalMargin;
        mPrecinctHeight = (int) (mPrecinctWidth * ((double) mOutHeight / (double) mOutWidth));

        mVerticalMargin = (getMeasuredHeight() - mPrecinctHeight) / 2;

        mClipImageView.reset(
                mIn,
                mOutWidth,
                mOutHeight,
                mPrecinctWidth,
                mPrecinctHeight,
                mVerticalMargin,
                mHorizontalMargin);
        mClipBorderView.reset(
                mVerticalMargin,
                mHorizontalMargin);
    }

    /* 画布视图 */
    private static class ClipImageView extends View {
        /**
         * MSCALE_X    MSKEW_X     MTRANS_X
         * MSKEW_Y     MSCALE_Y    MTRANS_Y
         * MPERSP_0    MPERSP_1    MPERSP_2
         * <p>
         * MSCALE 用于处理缩放变换
         * MSKEW 用于处理错切变换
         * MTRANS 用于处理平移变换
         * MPERSP 用于处理透视变换
         */
        private Matrix mMatrix;
        private Bitmap mOriginalBitmap;

        private float mScale = 1.0f;
        private float mDegrees = 0.0f;
        private float SCALE_MIN = 0;
        private float SCALE_MAX = 0;

        private Uri mIn;
        private int mOutWidth, mOutHeight;

        private int mPrecinctWidth, mPrecinctHeight;
        private int mVerticalMargin, mHorizontalMargin;

        /* 手势检测器 */
        private GestureDetector mGestureDetector;
        private ScaleGestureDetector mScaleGestureDetector;

        private OnTouchListener mOnTouchListener = new OnTouchListener() {
            private int lastPointerCount = 0;
            private float mLastX, mLastY, mLastA;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                /* 事件分发 */
                if (mGestureDetector.onTouchEvent(motionEvent)) return true;
                mScaleGestureDetector.onTouchEvent(motionEvent);

                /* 处理触摸事件 */
                float x = 0, y = 0;
                int pointerCount = motionEvent.getPointerCount();
                for (int i = 0; i < pointerCount; i++) {
                    x += motionEvent.getX(i);
                    y += motionEvent.getY(i);
                }
                x = x / pointerCount;
                y = y / pointerCount;

                /* 每当触摸点发生变化时，重置mLasX , mLastY */
                if (pointerCount != lastPointerCount) {
                    mLastX = x;
                    mLastY = y;
                }
//                /* 旋转和缩放冲突 */
//                if (pointerCount == 2) {
//                    float x1, y1, x2, y2, k, a;
//                    x1 = motionEvent.getX(0);
//                    y1 = motionEvent.getY(0);
//                    x2 = motionEvent.getX(1);
//                    y2 = motionEvent.getX(1);
//                    k = (y2 - y1) / (x2 - x1);
//                    a = (float) (180 / Math.PI * Math.atan(k));
//                    if (mLastA == 0) mLastA = a;
//                    mMatrix.postRotate(a - mLastA, x, y);
//                    mLastA = a;
//                    Log.e("A", "onTouch: " + a);
//                } else {
//                    mLastA = 0;
//                }
                lastPointerCount = pointerCount;

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = x - mLastX;
                        float dy = y - mLastY;

                        mMatrix.postTranslate(dx, dy);

                        checkBorder();

                        postInvalidate();

                        mLastX = x;
                        mLastY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        lastPointerCount = 0;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        lastPointerCount = 0;
                        break;
                }
                return true;
            }
        };

        private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float x = e.getX();
                float y = e.getY();
                if ((x > mHorizontalMargin && x < mHorizontalMargin + mPrecinctWidth) &&
                        (y > mVerticalMargin && y < mVerticalMargin + mPrecinctHeight)) {
                    float scale = 1.0f;
                    if (mScale == SCALE_MIN) {
                        scale = setScale(SCALE_MAX);
                    } else if (mScale == SCALE_MAX) {
                        scale = setScale(SCALE_MIN);
                    } else if (mScale >= (SCALE_MAX + SCALE_MIN) / 2) {
                        scale = setScale(SCALE_MAX);
                    } else {
                        scale = setScale(SCALE_MIN);
                    }
                    mMatrix.postScale(scale, scale, x, y);

                    checkBorder();

                    postInvalidate();
                    return true;
                }
                return false;
            }
        };

        private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                /* 当前伸缩比率 */
                float scale = scaleGestureDetector.getScaleFactor();

                float s = mScale * scale;
                if (s < SCALE_MIN) {
                    scale = setScale(SCALE_MIN);
                }
                if (s > SCALE_MAX) {
                    scale = setScale(SCALE_MAX);
                }

                scale = setPostScale(scale);

                mMatrix.postScale(scale, scale, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());

                postInvalidate();
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

            }
        };

        public ClipImageView(Context context) {
            super(context);

            mMatrix = new Matrix();

            setOnTouchListener(mOnTouchListener);

            mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
            mScaleGestureDetector = new ScaleGestureDetector(getContext(), mOnScaleGestureListener);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mOriginalBitmap != null && mMatrix != null) {
                canvas.drawBitmap(mOriginalBitmap, mMatrix, null);
            }
        }

        public void reset(Uri in, int outWidth, int outHeight, int precinctWidth, int precinctHeight, int verticalMargin, int horizontalMargin) {
            mIn = in;
            mOutWidth = outWidth;
            mOutHeight = outHeight;
            mPrecinctWidth = precinctWidth;
            mPrecinctHeight = precinctHeight;
            mVerticalMargin = verticalMargin;
            mHorizontalMargin = horizontalMargin;

            /* 取图片尺寸 */
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mIn.getPath(), opts);
            int original_width = opts.outWidth;
            int original_height = opts.outHeight;

            /* 计算缩放比例,取压缩后Bitmap */
            opts.inSampleSize = Math.max(original_width / getMeasuredWidth(), original_height / getMeasuredHeight());
            opts.inJustDecodeBounds = false;
            if (mOriginalBitmap != null) mOriginalBitmap.recycle();
            mOriginalBitmap = BitmapFactory.decodeFile(in.getPath(), opts);
            if (mOriginalBitmap == null) mOriginalBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);

            initMatrix();

            initZoom();
            initCentre();
        }

        public void rotate() {
            float degrees = mDegrees + 90.0f;

            initMatrix();

            float[] point = getBitmapCentre();
            mMatrix.postRotate(
                    setPostRotate(degrees),
                    point[0],
                    point[1]);

            initZoom();
            initCentre();

            postInvalidate();
        }

        public Bitmap clip() {
            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            draw(canvas);
            Matrix matrix = new Matrix();
            float out_width_scale = (float) mOutWidth / (float) mPrecinctWidth;
            float out_height_scale = (float) mOutHeight / (float) mPrecinctHeight;
            matrix.postScale(out_width_scale, out_height_scale);
            Bitmap clip = Bitmap.createBitmap(
                    bitmap,
                    mHorizontalMargin,
                    mVerticalMargin,
                    mPrecinctWidth,
                    mPrecinctHeight,
                    matrix,
                    true);
            bitmap.recycle();
            return clip;
        }

        /**
         * 取Matrix变换后Bitmap的实际位置
         * @return
         */
        private RectF getBitmapPosition() {
            RectF rect = new RectF(
                    0,
                    0,
                    mOriginalBitmap.getWidth(),
                    mOriginalBitmap.getHeight());
            mMatrix.mapRect(rect);
            return rect;
        }

        /**
         * 取Matrix变换后Bitmap的中心位置
         * @return
         */
        private float[] getBitmapCentre() {
            float[] point = new float[] {
                    mOriginalBitmap.getWidth() / 2,
                    mOriginalBitmap.getHeight() / 2};
            mMatrix.mapPoints(point);
            return point;
        }

        /**
         * 计算增量并存储当前缩放比
         * @param newscale
         * @return 新值
         */
        private float setScale(float newscale) {
            float scale = newscale / mScale;
            mScale = newscale;
            return scale;
        }

        /**
         * 增量存储当前缩放比并做校验
         * @param newscale
         * @return 新值
         */
        private float setPostScale(float newscale) {
            mScale = mScale * newscale;
            return newscale;
        }

        /**
         * 增量存储当前角度值
         * @param newdegrees
         * @return 新值
         */
        private float setPostRotate(float newdegrees) {
            mDegrees = (mDegrees + newdegrees) % 360.0f;
            return newdegrees;
        }

        /**
         * 初始化矩阵
         */
        private void initMatrix() {
            mMatrix.reset();
            mScale = 1.0f;
            mDegrees = 0.0f;
        }

        /**
         * 计算缩放比,缩放至不小于边框值
         */
        private void initZoom() {
            RectF rect = getBitmapPosition();
            float x_zoom = (float) mPrecinctWidth / rect.width();
            float y_zoom = (float) mPrecinctHeight / rect.height();

            SCALE_MIN = Math.max(x_zoom, y_zoom);
            SCALE_MAX = SCALE_MIN * 2;

            float[] point = getBitmapCentre();
            float scale = setPostScale(SCALE_MIN);
            mMatrix.postScale(scale, scale, point[0], point[1]);
        }

        /**
         * 移动至屏幕中心
         */
        private void initCentre() {
            int screen_x = getMeasuredWidth() / 2;
            int screen_y = getMeasuredHeight() / 2;

            float[] point = getBitmapCentre();
            int bitmap_x = (int) point[0];
            int bitmap_y = (int) point[1];

            mMatrix.postTranslate(screen_x - bitmap_x, screen_y - bitmap_y);
        }

        /**
         * 边界检测
         * 如果图片在裁剪框的左边像右移动,移到裁剪框里面.其他方向也一样,移动到裁剪框里面.
         */
        private void checkBorder() {
            RectF rect = getBitmapPosition();
            float deltaX = 0;
            float deltaY = 0;

            int width = getWidth();
            int height = getHeight();

            /* 如果宽或高大于屏幕，则控制范围 ; 这里的0.001是因为精度丢失会产生问题，但是误差一般很小，所以我们直接加了一个0.01 */
//            if (rect.width() + 0.01 >= width - 2 * mHorizontalMargin) {
            if (rect.left > mHorizontalMargin) {
                deltaX = -rect.left + mHorizontalMargin;
            }
            if (rect.right < width - mHorizontalMargin) {
                deltaX = width - mHorizontalMargin - rect.right;
            }
//            }
//            if (rect.height() + 0.01 >= height - 2 * mVerticalMargin) {
            if (rect.top > mVerticalMargin) {
                deltaY = -rect.top + mVerticalMargin;
            }
            if (rect.bottom < height - mVerticalMargin) {
                deltaY = height - mVerticalMargin - rect.bottom;
            }
//            }
            mMatrix.postTranslate(deltaX, deltaY);
        }
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

    /* 边框视图 */
    private static class ClipBorderView extends View {
        /* 线框宽度 1dp */
        private final static int WIDTH_BORDER = 1;

        private Paint mPaint;
        private PorterDuffXfermode mPorterDuffXfermode;

        private int mBorderWidth = 0;
        private int mVerticalMargin, mHorizontalMargin;

        public ClipBorderView(Context context) {
            super(context);

            mPaint = new Paint();
            mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

            mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH_BORDER, getResources().getDisplayMetrics());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int layer;
            int width = getWidth();
            int height = getHeight();

            mPaint.reset();

            // 要实现混合的两个图形，必须位于同一个layer上
            // 经测试位于不同layer上是无法混合的，即使最后都绘制到了canvas上
            layer = canvas.saveLayer(0, 0, width, height, mPaint, Canvas.ALL_SAVE_FLAG);

            // 绘制阴影
            mPaint.setXfermode(null);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(0xA0000000);
            canvas.drawRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    mPaint);

            // 绘制重叠
            mPaint.setXfermode(mPorterDuffXfermode);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(0xFF000000);
            canvas.drawRect(
                    mHorizontalMargin,
                    mVerticalMargin,
                    getWidth() - mHorizontalMargin,
                    getHeight() - mVerticalMargin,
                    mPaint);

            // 恢复已保存的图层
            canvas.restoreToCount(layer);

            // 绘制边框
            mPaint.setXfermode(null);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mBorderWidth);
            mPaint.setColor(0xFFFFFFFF);
            canvas.drawRect(
                    mHorizontalMargin,
                    mVerticalMargin,
                    getWidth() - mHorizontalMargin,
                    getHeight() - mVerticalMargin,
                    mPaint);
        }

        public void reset(int verticalMargin, int horizontalMargin) {
            mVerticalMargin = verticalMargin;
            mHorizontalMargin = horizontalMargin;

            postInvalidate();
        }
    }
}
