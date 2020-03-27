package com.liux.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.util.List;

/**
 * Created by Liux on 2016/12/8.
 */
public class UriUtil {

    /**
     * 获取框架自定义权限
     * @param context
     * @return
     */
    public static String getAuthority(Context context) {
        return context.getPackageName() + ".UriUtilFileProvider";
    }

    /**
     * 获取文件带 file:// 或 content:// 的 Uri
     * @param context
     * @param file
     * @return
     */
    public static Uri getProviderUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return UriUtilFileProvider.getUriForFile(context, getAuthority(context), file);
        }
        return Uri.fromFile(file);
    }

    /**
     * 给Intent设置数据和类型,并授予Uri读写权限
     * @param context
     * @param intent
     * @param type
     * @param file
     */
    public static void setIntentDataAndType(Context context, Intent intent, String type, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(getProviderUri(context, file), type);
    }

    /**
     * 手动对Intent匹配的应用程序进行Uri授权
     * 移除授权 {@link Context#revokeUriPermission(Uri, int)}
     * @param context
     * @param intent
     * @param uri
     */
    public static void grantUriPermissionForIntent(Context context, Intent intent, Uri uri) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfos == null || resolveInfos.isEmpty()) return;

        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }
}
