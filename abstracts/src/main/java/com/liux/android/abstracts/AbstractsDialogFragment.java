package com.liux.android.abstracts;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * 基础全屏沉浸式的 DialogFragment <br>
 * Created by Liux on 2018-12-1.
 */

public abstract class AbstractsDialogFragment extends AppCompatDialogFragment implements IAbstractsDialogFragment {
    private String TAG = "AbstractsDialogFragment";

    private AbstractsDialogFragmentProxy mProxy = new AbstractsDialogFragmentProxy(this);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mProxy.onActivityCreatedBefore();
        super.onActivityCreated(savedInstanceState);
        mProxy.onActivityCreatedAfter();
    }

    @Override
    public AppCompatDialogFragment getTarget() {
        return this;
    }

    @Override
    public void setMatchParentLayout(boolean width, boolean height) {
        mProxy.setMatchParentLayout(width, height);
    }
}
