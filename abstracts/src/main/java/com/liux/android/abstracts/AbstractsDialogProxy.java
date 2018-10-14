package com.liux.android.abstracts;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;

import com.liux.android.abstracts.util.DialogUtil;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

public class AbstractsDialogProxy {

    private IAbstractsDialog mIAbstractsDialog;

    public AbstractsDialogProxy(IAbstractsDialog IAbstractsDialog) {
        mIAbstractsDialog = IAbstractsDialog;
    }

    public void onContentChanged() {
        if (mFullScreen) {
            mIAbstractsDialog.getTarget().getWindow().setBackgroundDrawable(mBackground);
            mIAbstractsDialog.getTarget().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    // ===============================================================

    private boolean mFullScreen = false;
    private ColorDrawable mBackground = new ColorDrawable(Color.TRANSPARENT);

    public void openTranslucentMode() {
        DialogUtil.openTranslucentMode(mIAbstractsDialog.getTarget());
    }

    public boolean isFullScreen() {
        return mFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        mFullScreen = fullScreen;
    }

    public int getBackgroundColor() {
        return mBackground.getColor();
    }

    public void setBackgroundColor(int color) {
        mBackground = new ColorDrawable(color);
    }
}
