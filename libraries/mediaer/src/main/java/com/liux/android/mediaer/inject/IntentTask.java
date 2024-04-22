package com.liux.android.mediaer.inject;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public interface IntentTask extends Task {

    void onExecute(Fragment fragment, int requestCode);

    void onActivityResult(int resultCode, Intent data);
}
