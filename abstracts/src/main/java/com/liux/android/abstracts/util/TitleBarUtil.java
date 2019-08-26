package com.liux.android.abstracts.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

public class TitleBarUtil {

    /**
     * 获取沉浸式状态下状态栏的高度 <br>
     * 小于 KITKAT 表示不支持开启沉浸式,返回 0
     * @return
     */
    public static int getTransparentStatusBarHeight(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = activity.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     *  设置系统状态栏图标和字体配色模式
     * @param darkmode 是否深色模式
     * @return 成功执行返回true
     */
    public static boolean setStatusBarMode(Activity activity, boolean darkmode) {
        boolean miui = setMiuiStatusBarMode(activity, darkmode);
        boolean flyme = setMeizuStatusBarMode(activity, darkmode);
        boolean android = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = activity.getWindow().getDecorView();
            int ui = decor.getSystemUiVisibility();
            if (darkmode) {
                ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decor.setSystemUiVisibility(ui);
            android = true;
        }
        return miui || flyme || android;
    }

    /**
     * 设置 MIUIV6+ 系统状态栏图标和字体配色模式 <br>
     * 开发版 7.7.13 及以后版本和原生一致 <br>
     * http://www.miui.com/thread-8946673-1-1.html <br>
     *
     * @param activity 需要设置的Activity
     * @param darkmode 是否把状态栏字体及图标颜色设置为深色
     * @return 成功执行返回true
     */
    @SuppressLint("PrivateApi")
    private static boolean setMiuiStatusBarMode(Activity activity, boolean darkmode) {
        // 开发版 7.7.13 以前版本
        try {
            int darkModeFlag = 0;
            Class<? extends Window> clazz = activity.getWindow().getClass();
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod(darkmode ? "addExtraFlags" : "clearExtraFlags", int.class);
            extraFlagField.invoke(activity.getWindow(), darkModeFlag);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * 设置 Flyme 系统状态栏图标和字体配色模式 <br>
     * @param activity 需要设置的Activity
     * @param darkmode 是否把状态栏字体及图标颜色设置为深色
     * @return 成功执行返回true
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    private static boolean setMeizuStatusBarMode(Activity activity, boolean darkmode) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkmode) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * 取消Toolbar自带的边距,处理Toolbar可以自适应标题栏高度
     * @param view
     */
    public static void operationToolbar(View view) {
        try {
            Toolbar toolbar = (Toolbar) view.getParent();
            // 修改Toolbar边距
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.setMargins(0, 0, 0, 0);
            toolbar.setPadding(0, 0, 0, 0);
            toolbar.setLayoutParams(lp);
            toolbar.setContentInsetsAbsolute(0, 0);
            toolbar.setContentInsetsRelative(0, 0);

            // 设置Toolbar背景
            ((View) toolbar.getParent()).setBackgroundColor(Color.TRANSPARENT);

            // 设置Toolbar尺寸
            toolbar.setMinimumHeight(0);
            // 隐藏 MenuView
            toolbar.getMenu();
            Class clazz = toolbar.getClass();
            Field[] fields = clazz.getDeclaredFields();
            Field mMenuView = null;
            for (Field field : fields) {
                if (field.getType() == ActionMenuView.class) {
                    mMenuView = field;
                    break;
                }
            }
            if (mMenuView == null) return;
            mMenuView.setAccessible(true);
            ActionMenuView actionMenuView = (ActionMenuView) mMenuView.get(toolbar);
            actionMenuView.setVisibility(View.GONE);
        } catch (Exception ignore) {}
    }
}
