package com.liux.android.util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.List;

/**
 * Created by Liux on 2017/12/14.
 */

public class AppUtil {

    /**
     * 判断是否是主进程
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        List<ActivityManager.RunningAppProcessInfo> infos = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        String myName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == myPid && myName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是在前台运行
     * 兼容 Android L
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        if (infos.size() == 0) return false;
        for(ActivityManager.RunningAppProcessInfo process : infos) {
            if(process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && process.processName.equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断服务是否运行
     * @param context
     * @param service
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<? extends Service> service) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (list == null || list.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo info : list) {
            if (info.service.getClassName().equals(service.getName())) return true;
        }
        return false;
    }

    /**
     * 是否安装了某个app
     * @param context
     * @param packageName 包名
     **/
    public static boolean isAppInstalled(Context context, String packageName) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        if (packages != null && packages.size() > 0) {
            for (PackageInfo packageInfo : packages) {
                if (packageInfo.packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取应用ApplicationID
     * @param context
     * @return
     */
    public static String getApplicationID(Context context) {
        return context.getApplicationInfo().packageName;
        // return context.getApplicationInfo().processName;
        // return context.getPackageName();
    }

    /**
     * 获取应用包名
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取应用名称
     * @param context
     * @return
     */
    public static String getApplicationName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (PackageManager.NameNotFoundException ignore) {}
        return null;
    }

    /**
     * 获取应用版本号码
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException ignore) {}
        return -1;
    }

    /**
     * 获取应用版本名称
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (PackageManager.NameNotFoundException ignore) {}
        return null;
    }

    /**
     * 获取apk文件的包名
     * @param context
     * @param apkFile
     **/
    public static String getApkFilePackage(Context context, File apkFile) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkFile.getPath(), PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.applicationInfo.packageName;
        }
        return null;
    }
}
