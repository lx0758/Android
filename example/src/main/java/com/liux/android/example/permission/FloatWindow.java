package com.liux.android.example.permission;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.liux.android.example.R;
import com.liux.android.util.ScreenUtil;

public class FloatWindow {

    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;

    private View mFloatLayout;
    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;

    public FloatWindow(Context context) {
        mContext = context;

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        // 8.0新特性
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888
        );
        mWindowParams.gravity = Gravity.TOP | Gravity.START;

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        mFloatLayout = imageView;
        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int statusBarHeight = ScreenUtil.getStatusBarHeight(mContext);;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取相对View的坐标，即以此View左上角为原点
                        mInViewX = event.getX();
                        mInViewY = event.getY();
                        // 获取相对屏幕的坐标，即以屏幕左上角为原点
                        mDownInScreenX = event.getRawX();
                        mDownInScreenY = event.getRawY() - statusBarHeight;
                        mInScreenX = event.getRawX();
                        mInScreenY = event.getRawY() - statusBarHeight;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 更新浮动窗口位置参数
                        mInScreenX = event.getRawX();
                        mInScreenY = event.getRawY() - statusBarHeight;
                        mWindowParams.x = (int) (mInScreenX- mInViewX);
                        mWindowParams.y = (int) (mInScreenY - mInViewY);
                        // 手指移动的时候更新小悬浮窗的位置
                        mWindowManager.updateViewLayout(mFloatLayout, mWindowParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                        if (mDownInScreenX  == mInScreenX && mDownInScreenY == mInScreenY){

                        }
                        break;
                }
                return true;
            }
        });
    }

    public void showFloatWindow(){
        if (mFloatLayout.getParent() == null){
            DisplayMetrics metrics = new DisplayMetrics();
            //默认固定位置，靠屏幕右边缘的中间
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.x = metrics.widthPixels;
            mWindowParams.y = metrics.heightPixels / 2 - ScreenUtil.getStatusBarHeight(mContext);
            mWindowManager.addView(mFloatLayout, mWindowParams);
        }
    }

    public void hideFloatWindow(){
        if (mFloatLayout.getParent() != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }

    public void setFloatLayoutAlpha(float alpha){
        if (alpha > 1) alpha = 1;
        if (alpha < 0) alpha = 0;
        mFloatLayout.setAlpha(alpha);
    }
}