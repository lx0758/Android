package com.liux.android.tool;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

/**
 * 精确的 Handler 定时器
 * Created by Liux on 2017/01/05
 */

public class CountDownTimer {
    private static final int MSG = 1;

    private int mRequestCode = 0;
    private boolean mRepetition = false;

    private long mLastTime;
    private long mGrossTime;
    private long mSurplusTime;
    private long mIntervalTime = 1000;

    private OnTimerListener mOnTimerListener;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg){
            try {
                long intervalTime = nextIntervalTime();
                if (mRepetition) {
                    sendEmptyMessageDelayed(MSG, intervalTime);
                    if (mOnTimerListener != null) mOnTimerListener.onTick(mRequestCode, 0);
                } else {
                    if (intervalTime > 0) {
                        mSurplusTime = mSurplusTime - intervalTime;
                    } else {
                        mSurplusTime = mSurplusTime + intervalTime;
                    }
                    if (mSurplusTime > mIntervalTime / 2) {
                        sendEmptyMessageDelayed(MSG, intervalTime);
                        if (mOnTimerListener != null) mOnTimerListener.onTick(mRequestCode, mSurplusTime);
                    } else {
                        mHandler.removeMessages(MSG);
                        if (mOnTimerListener != null) mOnTimerListener.onFinish(mRequestCode);
                    }
                }
            } catch (Exception e) {
                if (mHandler != null) mHandler.removeMessages(MSG);
                e.printStackTrace();
            }
        }

        /**
         * 计算下一次间隔时间[精确计时]
         * @return
         */
        private long nextIntervalTime() {
            long now = System.currentTimeMillis();

            // 当前时间和上次时间的时间差
            long difference = now - mLastTime;

            // 假设时间差是   30, 间隔是 1000, 则下次时间应该是  970 = 1000 - (  30 % 1000)
            // 假设时间差是  960, 间隔是 1000, 则下次时间应该是 1040 = 1000 - ( 960 % 1000 - 1000)
            // 假设时间差是 1080, 间隔是 1000, 则下次时间应该是  920 = 1000 - (1080 % 1000)
            // 假设时间差是 1970, 间隔是 1000, 则下次时间应该是 1030 = 1000 - (1970 % 1000 - 1000)
            long interval;
            if (difference % mIntervalTime < mIntervalTime / 2) {
                interval =  mIntervalTime - difference;
            } else {
                interval = mIntervalTime - (difference - mIntervalTime);
            }

            mLastTime = now;
            return interval;
        }
    };

    public CountDownTimer() {

    }

    /* 循环模式 */
    public CountDownTimer(long interval) {
        mRepetition = true;
        this.mIntervalTime = interval;
    }

    /* 计时模式 */
    public CountDownTimer(long gross, long interval) {
        mRepetition = false;
        this.mGrossTime = gross;
        this.mIntervalTime = interval;
    }

    public boolean isRepetition() {
        return mRepetition;
    }

    public void setRepetition(boolean repetition) {
        this.mRepetition = repetition;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    public void setRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
    }

    public long getGrossTime() {
        return mGrossTime;
    }

    public void setGrossTime(long grossTime) {
        this.mGrossTime = grossTime;
    }

    public long getIntervalTime() {
        return mIntervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.mIntervalTime = intervalTime;
    }

    public void setOnTimerListener(OnTimerListener listener) {
        this.mOnTimerListener = listener;
    }

    public void start() {
        mLastTime = System.currentTimeMillis();
        mSurplusTime = mGrossTime + mIntervalTime;

        mHandler.removeMessages(MSG);
        mHandler.sendEmptyMessage(MSG);
    }

    public void stop() {
        mHandler.removeMessages(MSG);
    }

    public void reset() {
        if (mOnTimerListener != null) mOnTimerListener.onReset(mRequestCode);
        stop();
    }

    public void finish() {
        if (mOnTimerListener != null) mOnTimerListener.onFinish(mRequestCode);
        stop();
    }

    public boolean isRun() {
        return mSurplusTime > 0;
    }

    public interface OnTimerListener {

        void onReset(int requestCode);

        void onTick(int requestCode, long surplus);

        void onFinish(int requestCode);
    }

    public static class Builder {
        private CountDownTimer mCountDownTimer;

        public Builder() {
            mCountDownTimer = new CountDownTimer();
        }

        public Builder repetition(boolean repetition) {
            mCountDownTimer.setRepetition(repetition);
            return this;
        }

        public Builder requestCode(int requestCode) {
            mCountDownTimer.setRequestCode(requestCode);
            return this;
        }

        public Builder gross(long gross) {
            mCountDownTimer.setGrossTime(gross);
            return this;
        }

        public Builder interval(long interval) {
            mCountDownTimer.setIntervalTime(interval);
            return this;
        }

        public Builder listener(OnTimerListener listener) {
            mCountDownTimer.setOnTimerListener(listener);
            return this;
        }

        public CountDownTimer build() {
            return mCountDownTimer;
        }
    }
}