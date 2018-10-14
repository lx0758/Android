package com.liux.android.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Liux on 2017/12/14.
 */

public class AppUtil {

    /**
     * 通过反射获取Activity列表
     * @return
     */
    public static List<Activity> getActivity() {
        ArrayList<Activity> activitiys = new ArrayList<>();
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                activitiys.add(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activitiys;
    }

    /**
     * 通过反射获取顶层Activity
     * @return
     */
    public static Activity getTopActivity() {
        List<Activity> activitiys = getActivity();
        if (activitiys.isEmpty()) return null;
        return activitiys.get(activitiys.size() - 1);
    }

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
     * 是否MIUI系统
     * @return
     */
    public static boolean isMIUI() {
        return "Xiaomi".toLowerCase().equals(Build.MANUFACTURER.toLowerCase());
    }

    /**
     * 是否魅族系统
     * @return
     */
    public static boolean isMEIZU() {
        return "Meizu".toLowerCase().equals(Build.MANUFACTURER.toLowerCase());
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
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     * 获取应用版本号码
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Null";
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

    /**
     * 获取设备名称
     * @return
     */
    public static String getDeviceName() {
        return Build.MODEL;
    }

    /**
     * 获取系统名称
     * @return
     */
    public static String getOSName() {
        return "Android";
    }

    /**
     * 获取系统版本
     * @return
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }
}
