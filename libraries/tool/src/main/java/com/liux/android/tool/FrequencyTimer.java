package com.liux.android.tool;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

/**
 * 次数定时器
 * Created by Liux on 2017/01/05
 */

public abstract class FrequencyTimer {
    private static final int MSG = 1;

    // 是否循环
    private boolean mRepetition;
    // 触发间隔(秒)
    private int mIntervalTime;
    // 总触发次数
    private int mTotalFrequency;

    // 是否在工作
    private boolean mRuning = false;
    // 已经触发次数
    private int mTickFrequency = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg){
            mTickFrequency ++;
            if (mRepetition) {
                onTick(mTickFrequency, 0);
                sendEmptyMessageDelayed(MSG, mIntervalTime * 1000);
            } else {
                if (mTickFrequency < mTotalFrequency) {
                    onTick(mTickFrequency, mTotalFrequency - mTickFrequency);
                    sendEmptyMessageDelayed(MSG, mIntervalTime * 1000);
                } else {
                    mHandler.removeMessages(MSG);
                    mRuning = false;
                    onFinish();
                }
            }
        }
    };

    public FrequencyTimer() {
        this(1);
    }

    /* 循环模式 */
    public FrequencyTimer(int interval) {
        if (interval <= 0) interval = 1;
        mRepetition = true;
        mIntervalTime = interval;
    }

    /* 计次模式 */
    public FrequencyTimer(int interval, int total) {
        if (interval <= 0) interval = 1;
        mRepetition = false;
        mIntervalTime = interval;
        mTotalFrequency = total;
    }

    public void start() {
        mRuning = true;
        mTickFrequency = 0;

        mHandler.removeMessages(MSG);
        mHandler.sendEmptyMessageDelayed(MSG, mIntervalTime * 1000L);

        onStart();
    }

    public void stop() {
        mHandler.removeMessages(MSG);
        mRuning = false;

        onStop();
    }

    public boolean isRun() {
        return mRuning;
    }

    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract void onTick(int tickFrequency, int surplusFrequency);

    protected abstract void onFinish();
}