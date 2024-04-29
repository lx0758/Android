package com.liux.android.multimedia.inject;

import androidx.fragment.app.Fragment;

public interface CallTask extends Task {

    void onExecute(Fragment fragment);
}
