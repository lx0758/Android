package com.liux.android.permission.floats;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.liux.android.permission.PermissionFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

public class FloatPermissionUtil {

    private static Properties properties;

    public static String getSystemProperty(String key) {
        if (properties == null) loadProperties();
        return properties.getProperty(key);
    }

    public static boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 魅族需要单独适配一下
            if (MeizuUtil.isMeizuRom()) {
                return hasPermissionMBefore(context);
            }
            return Settings.canDrawOverlays(context);
        } else {
            return hasPermissionMBefore(context);
        }
    }

    public static boolean hasPermissionForResult(Context context) {
        return hasPermission(context);
    }

    protected static void gotoPermissionApply(int requestCode, PermissionFragment fragment) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (MeizuUtil.isMeizuRom()) {
                try {
                    MeizuUtil.gotoPermissionApply(requestCode, fragment);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            GoogleUtil.gotoPermissionApply(requestCode, fragment);
        } else {
            try {
                if (HuaweiUtil.isHuaweiRom()) {
                    HuaweiUtil.gotoPermissionApply(requestCode, fragment);
                } else if (MeizuUtil.isMeizuRom()) {
                    MeizuUtil.gotoPermissionApply(requestCode, fragment);
                } else if (MiuiUtil.isMiuiRom()) {
                    MiuiUtil.gotoPermissionApply(requestCode, fragment);
                } else if (OppoUtil.isOppoRom()) {
                    OppoUtil.gotoPermissionApply(requestCode, fragment);
                } else if (QikuUtil.isQikuRom()) {
                    QikuUtil.gotoPermissionApply(requestCode, fragment);
                } else {
                    GoogleUtil.gotoPermissionApply(requestCode, fragment);
                }
            } catch (Exception e) {
                e.printStackTrace();
                GoogleUtil.gotoPermissionApply(requestCode, fragment);
            }
        }
    }

    /**
     * 判断 6.0 以下是否有权限
     * 理论上 6.0 以上才需处理权限，但有的国内 ROM 在 6.0 以下就添加了权限
     * 其实此方式也可以用于判断 6.0 以上版本，只不过有更简单的 canDrawOverlays 代替
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean hasPermissionMBefore(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return true;

//            return AppOpsManager.MODE_ALLOWED == AppOpsManagerCompat.noteOp(
//                    context,
//                    AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
//                    Binder.getCallingUid(),
//                    context.getPackageName()
//            );

            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Method dispatchMethod = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
            // AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            return AppOpsManager.MODE_ALLOWED == (Integer) dispatchMethod.invoke(
                    manager,
                    24,
                    Binder.getCallingUid(),
                    context.getPackageName()
            );
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 载入系统配置
     */
    private static void loadProperties() {
        properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            properties.load(inputStream);
        } catch (Exception ignore) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {}
            }
        }
    }
}
