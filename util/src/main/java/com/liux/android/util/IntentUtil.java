package com.liux.android.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;

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
    public static void startGeneralMapNavigator(Context context, double lat, double lng) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(String.format("geo:%f,%f", lat, lng)));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "您尚未安装地图软件或无权限调用.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * 启动百度地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(bd09ll)
     * @param lng     终点经度(bd09ll)
     */
    public static void startBaiduMapNavigator(Context context, double lat, double lng, String name) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.baidu.BaiduMap");
            intent.setData(Uri.parse(String.format("baidumap://map/direction?destination=latlng:%f,%f|name:%s&mode=driving", lat, lng, name)));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "您尚未安装百度地图软件或无权限调用.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * 启动百度地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static void startAMapNavigator(Context context, double lat, double lng, String name) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // intent.setPackage("com.autonavi.minimap");
            intent.setData(Uri.parse(String.format("androidamap://route?sourceApplication=Back&dlat=%f&dlon=%f&dname=%s&dev=0&m=0&t=2", lat, lng, name)));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "您尚未安装高德地图软件或无权限调用.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * 启动腾讯地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static void startQQMapNavigator(Context context, double lat, double lng, String name) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.tencent.map");
            intent.setData(Uri.parse(String.format("http://apis.map.qq.com/uri/v1/routeplan?type=drive&to=%s&tocoord=%f,%f&coord_type=1&referer=Back", name, lat, lng)));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "您尚未安装腾讯地图软件或无权限调用.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * 启动谷歌地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static void startGoogleMapNavigator(Context context, double lat, double lng) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.apps.maps");
            intent.setData(Uri.parse(String.format("http://ditu.google.cn/maps?f=d&source=s_d&daddr=%f,%f&hl=zh", lat, lng)));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "您尚未安装谷歌地图软件或无权限调用.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * 调用系统呼叫电话
     *
     * @param context
     * @param number  呼叫号码
     */
    public static void callPhone(Context context, String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "找不到关联的程序,发起拨号失败!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 调用系统发送短信
     *
     * @param context
     * @param number  发送号码
     * @param content 短信内容
     */
    public static void sendSMS(Context context, String number, String content) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + number));
            intent.putExtra("sms_body", content);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "找不到关联的程序,短信发送失败!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 前往应用设置
     * @param context
     */
    public static void startApplicationSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "找不到关联的程序!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 打开网络设置界面
     * @param context
     */
    public static void startNetworkSetting(Context context) {
        try {
            Intent intent = new Intent("/");
            ComponentName cm = new ComponentName("com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(cm);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "找不到关联的程序!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 设置MIUI的神隐模式
     *
     * @param context
     * @param pkgname 自定义包名,为空代表自己
     * @param appname 自定义应用名,为空代表自己
     * @return 返回结果
     */
    public static boolean setMIUIPowerKeeper(Context context, String pkgname, String appname) {
        if (!AppUtil.isMIUI()) return false;
        try {
            Intent intent = new Intent("miui.intent.action.HIDDEN_APPS_CONFIG_ACTIVITY");
            intent.putExtra("package_name", pkgname != null ? pkgname : AppUtil.getPackageName(context));
            intent.putExtra("package_label", appname != null ? appname : AppUtil.getApplicationName(context));
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 安装Apk文件
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            // 华为EMUI说,我必须要这句
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "没有找到程序安装器,软件安装失败!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "参数错误.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 卸载某个App
     * @param context
     * @param packageName app包名
     **/
    public static void unInstallApp(Context context, String packageName) {
        try {
            Uri packageUri = Uri.parse("package:" + packageName);
            Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "没有找到程序安装器,软件安装\\卸载失败!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 启动某个App
     * @param context
     * @param pkgName app包名
     **/
    public static void startApp(Context context, String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
        if (intent == null) {
            Toast.makeText(context, "没有找到App", Toast.LENGTH_SHORT).show();
            return;
        }
        context.startActivity(intent);
    }

    public static void callAlbum(Activity activity, int requestCode) {
        callAlbum(activity, activity, requestCode);
    }

    public static void callAlbum(Fragment fragment, int requestCode) {
        callAlbum(fragment.getActivity(), fragment, requestCode);
    }

    public static void callAlbum(android.support.v4.app.Fragment fragment, int requestCode) {
        callAlbum(fragment.getActivity(), fragment, requestCode);
    }

    private static void callAlbum(Context context, ComponentCallbacks callbacks, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        try {
            if (callbacks instanceof Activity) {
                ((Activity) callbacks).startActivityForResult(intent, requestCode);
            } else if (callbacks instanceof Fragment) {
                ((Fragment) callbacks).startActivityForResult(intent, requestCode);
            } else if (callbacks instanceof android.support.v4.app.Fragment) {
                ((android.support.v4.app.Fragment) callbacks).startActivityForResult(intent, requestCode);
            }
        } catch (ActivityNotFoundException e){
            Toast.makeText(context, "没有找到合适的相册程序.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "参数错误.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void callCamera(Activity activity, Uri out, int requestCode) {
        callCamera(activity, activity, out, requestCode);
    }

    public static void callCamera(Fragment fragment, Uri out, int requestCode) {
        callCamera(fragment.getActivity(), fragment, out, requestCode);
    }

    public static void callCamera(android.support.v4.app.Fragment fragment, Uri out, int requestCode) {
        callCamera(fragment.getActivity(), fragment, out, requestCode);
    }

    private static void callCamera(Context context, ComponentCallbacks callbacks, Uri out, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, out);
        intent.putExtra("return-data", false);

        try {
            if (callbacks instanceof Activity) {
                ((Activity) callbacks).startActivityForResult(intent, requestCode);
            } else if (callbacks instanceof Fragment) {
                ((Fragment) callbacks).startActivityForResult(intent, requestCode);
            } else if (callbacks instanceof android.support.v4.app.Fragment) {
                ((android.support.v4.app.Fragment) callbacks).startActivityForResult(intent, requestCode);
            }
        } catch (ActivityNotFoundException e){
            Toast.makeText(context, "没有找到合适的相机程序.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "参数错误.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void callCrop(Activity activity, Uri in, Uri out, int out_width, int out_height, int requestCode) {
        callCrop(activity, activity, in, out, out_width, out_height, requestCode);
    }

    public static void callCrop(Fragment fragment, Uri in, Uri out, int out_width, int out_height, int requestCode) {
        callCrop(fragment.getActivity(), fragment, in, out, out_width, out_height, requestCode);
    }

    public static void callCrop(android.support.v4.app.Fragment fragment, Uri in, Uri out, int out_width, int out_height, int requestCode) {
        callCrop(fragment.getActivity(), fragment, in, out, out_width, out_height, requestCode);
    }

    private static void callCrop(Context context, ComponentCallbacks callbacks, Uri in, Uri out, int out_width, int out_height, int requestCode) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(in, "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", out_width);// 裁剪框比例
        intent.putExtra("aspectY", out_height);
        intent.putExtra("outputX", out_width);// 输出图片大小
        intent.putExtra("outputY", out_height);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, out);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        try {
            if (callbacks instanceof Activity) {
                ((Activity) callbacks).startActivityForResult(intent, requestCode);
            } else if (callbacks instanceof Fragment) {
                ((Fragment) callbacks).startActivityForResult(intent, requestCode);
            } else if (callbacks instanceof android.support.v4.app.Fragment) {
                ((android.support.v4.app.Fragment) callbacks).startActivityForResult(intent, requestCode);
            }
        } catch (ActivityNotFoundException e){
            Toast.makeText(context, "没有找到合适的裁剪程序.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "参数错误.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
