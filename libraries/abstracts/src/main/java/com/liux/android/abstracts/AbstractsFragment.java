package com.liux.android.abstracts;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liux.android.abstracts.touch.TouchCallback;
import com.liux.android.abstracts.touch.TouchHost;

/**
 * 抽象Fragment,提供以下能力 <br>
 * 1.自动隐藏输入法 {@link TouchHost} <br>
 */

public abstract class AbstractsFragment extends Fragment {

    @Nullable
    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    // ===============================================================

    protected boolean isHandlerTouch() {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return false;
        if (!touchHost.isHandlerTouch()) return false;
        if (touchHost.hasIgnoreView(getView())) return false;
        return true;
    }

    protected void setHandlerTouch(boolean handlerTouch) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        if (!touchHost.isHandlerTouch()) return;
        if (handlerTouch) {
            touchHost.removeIgnoreView(getView());
        } else {
            touchHost.addIgnoreView(getView());
        }
    }

    protected boolean hasIgnoreView(View view) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return false;
        return touchHost.hasIgnoreView(view);
    }

    protected void addIgnoreView(View view) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        touchHost.addIgnoreView(view);
    }

    protected void removeIgnoreView(View view) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        touchHost.removeIgnoreView(view);
    }

    protected void addTouchCallback(TouchCallback touchCallback) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        touchHost.addTouchCallback(touchCallback);
    }

    protected void removeTouchCallback(TouchCallback touchCallback) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        touchHost.removeTouchCallback(touchCallback);
    }

    private TouchHost getHandlerTouch() {
        Activity activity = requireActivity();
        if (activity instanceof TouchHost) {
            return (TouchHost) activity;
        }
        return null;
    }
}
