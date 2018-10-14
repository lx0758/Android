package com.liux.android.permission.floats;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import com.liux.android.permission.PermissionFragment;

class MiuiUtil {

    protected static boolean isMiuiRom() {
        return !TextUtils.isEmpty(FloatPermissionUtil.getSystemProperty("ro.miui.ui.version.name"));
    }

    protected static void gotoPermissionApply(int requestCode, PermissionFragment fragment) throws Exception {
        int versionCode = getMiuiVersion();
        switch (versionCode) {
            case 5:
                goToMiuiPermissionActivity_V5(requestCode, fragment);
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                goToMiuiPermissionActivity_V6789(requestCode, fragment);
                break;
            default:
                break;
        }
    }

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    private static int getMiuiVersion() {
        String version = FloatPermissionUtil.getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception ignore) {

            }
        }
        return -1;
    }

    /**
     * 小米 V5 版本 ROM权限申请
     * @param requestCode
     * @param fragment
     */
    private static void goToMiuiPermissionActivity_V5(int requestCode, PermissionFragment fragment) throws Exception {
        Intent intent = null;
        String packageName = fragment.getActivity().getPackageName();
        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 小米 V6789 版本 ROM权限申请
     * @param requestCode
     * @param fragment
     */
    private static void goToMiuiPermissionActivity_V6789(int requestCode, PermissionFragment fragment) throws Exception {
        try {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            //intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_pkgname", fragment.getActivity().getPackageName());
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_pkgname", fragment.getActivity().getPackageName());
            fragment.startActivityForResult(intent, requestCode);
        }
    }
}
