package org.android.framework.ui.provider;

/**
 * 2018/2/11
 * By Liux
 * lx0758@qq.com
 */

public interface IToast {

    void showInfo(CharSequence charSequence);

    void showWarn(CharSequence charSequence);

    void showError(CharSequence charSequence);

    void showSuccess(CharSequence charSequence);
}
