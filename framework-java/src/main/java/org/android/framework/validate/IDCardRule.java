package org.android.framework.validate;

import android.widget.TextView;

import org.android.framework.validate.util.IDVerificationUtil;


/**
 * date：2018/5/10 16:29
 * author：t.simeon
 * email：tsimeon@qq.com
 * description：身份证验证规则
 */
public class IDCardRule extends Rule {


    private String number;
    private static IDCardRule mIDCardRule;

    private String message;

    private IDCardRule(String number) {

    }

    public static IDCardRule getInstance(String number) {
        if (mIDCardRule == null) {
            mIDCardRule = new IDCardRule(number);
        }
        mIDCardRule.number = number;
        return mIDCardRule;
    }

    public static IDCardRule validate(String number){
        return getInstance(number);
    }

    public static IDCardRule validate(TextView textView, boolean trim){
        String number = textView.getText().toString();
        if(trim){
            number = number.trim();
        }
        return validate(number);
    }

    @Override
    public boolean valid() {
        message = IDVerificationUtil.validate_effective(number);
        return number != null && number.equalsIgnoreCase(message);
    }
}
