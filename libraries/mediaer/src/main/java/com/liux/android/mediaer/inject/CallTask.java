package com.liux.android.mediaer.inject;

import androidx.fragment.app.Fragment;

public interface CallTask extends Task {

    void onExecute(Fragment fragment);
}
