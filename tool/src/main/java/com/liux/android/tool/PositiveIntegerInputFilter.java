package com.liux.android.tool;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.Arrays;

/**
 * 过滤0开头的正整数
 * 2018/3/28
 * By Liux
 * lx0758@qq.com
 */

public class PositiveIntegerInputFilter implements InputFilter {

    private EditText mEditText;

    public static void install(EditText editText) {
        InputFilter[] old_filter = editText.getFilters();
        if (old_filter == null) old_filter = new InputFilter[0];

        InputFilter[] new_filter = Arrays.copyOf(old_filter, old_filter.length + 1);
        new_filter[old_filter.length] = new PositiveIntegerInputFilter(editText);

        editText.setFilters(new_filter);
    }

    public PositiveIntegerInputFilter(EditText editText) {
        mEditText = editText;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();

        // 当输入是在开头,并且输入内容前几位为"0"时,替换掉所有"0"
        if (sourceText.matches("^0+.*$") && dstart == 0){
            sourceText = sourceText.replaceAll("^0+", "");
            if (sourceText.isEmpty()) {
                if (destText.isEmpty()) return "0";
                if (destText.equals("0")) return "";
            }
        }

        // 处理开头位置的删除和替换
        if (dstart != dend && dstart == 0) {
            if (sourceText.isEmpty()) {
                // 删除
                String surplus = destText.substring(dend, destText.length());
                if (surplus.matches("^0+.*$")) {
                    surplus = surplus.replaceAll("^0+", "");
                    mEditText.setText(surplus);
                    return "";
                }
            } else {
                // 替换
                String surplus = sourceText + destText.substring(dend, destText.length());
                if (surplus.matches("^0+.*$")) {
                    surplus = surplus.replaceAll("^0+", "");
                    mEditText.setText(surplus);
                    return "";
                }
            }
        }

        // 处理当首位是0时输入其他字符
        if (destText.equals("0")) {
            mEditText.setText(sourceText);
            mEditText.setSelection(sourceText.length());
            return "";
        }

        return sourceText;
    }
}
