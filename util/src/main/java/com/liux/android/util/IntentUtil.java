package com.liux.android.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;

import java.util.Locale;

/**
 * IntentUtil
 * 2017/3/22
 *
 * @author Liux
 */

public class IntentUtil {

    /**
     * 启动通用的地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static Intent startGeneralMapNavigator(Context context, double lat, double lng) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(String.format(Locale.CHINA, "geo:%f,%f", lat, lng)));
        return intent;
    }

    /**
     * 启动百度地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(bd09ll)
     * @param lng     终点经度(bd09ll)
     */
    public static Intent startBaiduMapNavigator(Context context, double lat, double lng, String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.baidu.BaiduMap");
        intent.setData(Uri.parse(String.format(Locale.CHINA, "baidumap://map/direction?destination=latlng:%f,%f|name:%s&mode=driving", lat, lng, name)));
        return intent;
    }

    /**
     * 启动百度地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static Intent startAMapNavigator(Context context, double lat, double lng, String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.setPackage("com.autonavi.minimap");
        intent.setData(Uri.parse(String.format(Locale.CHINA, "androidamap://route?sourceApplication=Back&dlat=%f&dlon=%f&dname=%s&dev=0&m=0&t=2", lat, lng, name)));
        return intent;
    }

    /**
     * 启动腾讯地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static Intent startQQMapNavigator(Context context, double lat, double lng, String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.tencent.map");
        intent.setData(Uri.parse(String.format(Locale.CHINA, "http://apis.map.qq.com/uri/v1/routeplan?type=drive&to=%s&tocoord=%f,%f&coord_type=1&referer=Back", name, lat, lng)));
        return intent;
    }

    /**
     * 启动谷歌地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static Intent startGoogleMapNavigator(Context context, double lat, double lng) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.google.android.apps.maps");
        intent.setData(Uri.parse(String.format(Locale.CHINA, "http://ditu.google.cn/maps?f=d&source=s_d&daddr=%f,%f&hl=zh", lat, lng)));
        return intent;
    }

    /**
     * 调用系统呼叫电话
     *
     * @param context
     * @param number  呼叫号码
     */
    public static Intent callPhone(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        return intent;
    }

    /**
     * 调用系统发送短信
     *
     * @param context
     * @param number  发送号码
     * @param content 短信内容
     */
    public static Intent sendSMS(Context context, String number, String content) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + number));
        intent.putExtra("sms_body", content);
        return intent;
    }

    /**
     * 前往应用设置
     * @param context
     */
    public static Intent startApplicationSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * 打开网络设置界面
     * @param context
     * @return
     */
    public static Intent startNetworkSetting(Context context) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        return intent;
    }

    /**
     * 安装Apk文件
     *
     * @param context
     * @param uri
     */
    public static Intent installApk(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        // 华为EMUI说,我必须要这句
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return intent;
    }

    /**
     * 卸载某个App
     * @param context
     * @param packageName app包名
     *
     * @return*/
    public static Intent unInstallApp(Context context, String packageName) {
        Uri packageUri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * 启动某个App
     * @param context
     * @param pkgName app包名
     *
     * @return*/
    public static Intent startApp(Context context, String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
        return intent;
    }

    public static Intent callAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return intent;
    }

    private static Intent callCamera(Uri out) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, out);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("return-data", false);
        return intent;
    }

    private static Intent callCrop(Context context, Uri in, Uri out, int out_width, int out_height) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(in, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        // 裁剪框比例
        intent.putExtra("aspectX", out_width);
        intent.putExtra("aspectY", out_height);
        // 输出图片大小
        intent.putExtra("outputX", out_width);
        intent.putExtra("outputY", out_height);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, out);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return intent;
    }
}
