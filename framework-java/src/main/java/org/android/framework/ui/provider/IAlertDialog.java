package org.android.framework.ui.provider;

import androidx.annotation.StringRes;

/**
 * 2018/2/11
 * By Liux
 * lx0758@qq.com
 */

public interface IAlertDialog extends IDialog {

    void setTitle(CharSequence title);
    
    void setTitle(@StringRes int title);
    
    void setContent(CharSequence content);
    
    void setContent(@StringRes int content);

    void setPositiveButton(CharSequence buttonText, OnClickListener listener);
    
    void setPositiveButton(@StringRes int buttonText, OnClickListener listener);
    
    void setNegativeButton(CharSequence buttonText, OnClickListener listener);
    
    void setNegativeButton(@StringRes int buttonText, OnClickListener listener);
}
