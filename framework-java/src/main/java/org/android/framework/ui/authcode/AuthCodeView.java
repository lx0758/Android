package org.android.framework.ui.authcode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class AuthCodeView {
    private static final int COUNTDOWN_TIME = 90;
    private static final int COUNTDOWN_WHAT = 10;

    private int time;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (time > 0) {
                onCountdown(time);
                time --;
                sendEmptyMessageDelayed(COUNTDOWN_WHAT, 1000);
            } else {
                onCountdownEnd();
            }
        }
    };

    protected abstract void onRefreshImageCode();

    protected abstract void onSendStart();

    protected abstract void onSendFailure(String message);

    protected abstract void onSendCancel();

    protected abstract void onCountdown(int time);

    protected abstract void onCountdownEnd();

    public void onSendSucceed(String message) {
        startCountdown();
    }

    public void onDestroy() {
        stopCountdown();
    }

    private void startCountdown() {
        time = COUNTDOWN_TIME;
        handler.sendEmptyMessage(COUNTDOWN_WHAT);
    }

    private void stopCountdown() {
        handler.removeMessages(COUNTDOWN_WHAT);
    }
}
