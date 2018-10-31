package com.liux.android.boxing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;

import java.io.File;

/**
 * 调用系统相机工具,已经处理权限问题(Android 6.0 / 7.0)
 * Created by Liux on 2018/09/07.
 */

public class Cameraer {
    /* 没有找到应用程序 */
    public static final int ERROR_INTENT = 1;
    /* 没有拍照权限 */
    public static final int ERROR_PERMISSION = 2;

    private static String AUTHORITY;

    public static void setAuthority(String authority) {
        AUTHORITY = authority;
    }

    public static void start(Fragment fragment, OnCameraListener listener) {
        start(fragment.getActivity(), listener);
    }

    public static void start(android.support.v4.app.Fragment fragment, OnCameraListener listener) {
        start(fragment.getActivity(), listener);
    }

    public static void start(Activity activity, OnCameraListener listener) {
        getCameraFragment(activity).start(activity, listener);
    }

    private static CameraFragment getCameraFragment(Activity activity) {
        CameraFragment fragment;

        fragment = (CameraFragment) activity.getFragmentManager().findFragmentByTag(CameraFragment.TAG);

        if (fragment == null) fragment = new CameraFragment();

        return fragment;
    }

    @SuppressLint("ValidFragment")
    public static class CameraFragment extends Fragment {
        private static final String TAG = "CameraFragment";

        private OnCameraListener onCameraListener;

        private File file;
        private int requestCodeCamera = getRequestCode();
        private int requestCodePermissions = getRequestCode();

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCodeCamera != requestCode) return;
            if (Activity.RESULT_OK != resultCode) return;
            callSucceed();
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode != requestCodePermissions) return;

            for (String p : permissions) {
                if (!Manifest.permission.CAMERA.equals(p)) continue;

                if (PermissionChecker.checkCallingOrSelfPermission(getActivity(), Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
                    callCamera();
                    return;
                }
            }
            callFailure(ERROR_PERMISSION);
        }

        public void start(Activity activity, OnCameraListener onCameraListener) {
            this.onCameraListener = onCameraListener;

            file = new File(activity.getCacheDir().getAbsolutePath() + File.separator + "camera_" + String.valueOf(System.currentTimeMillis()));

            if (!isAdded()) addSelf(activity);

            callCameraAndCheckPermission();
        }

        /**
         * 获取一个随机的请求码
         * @return
         */
        private int getRequestCode() {
            return (int) (System.currentTimeMillis() & 0xFFFF);
        }

        @TargetApi(Build.VERSION_CODES.M)
        private void callCameraAndCheckPermission() {
            if (PermissionChecker.checkCallingOrSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        requestCodePermissions
                );
                return;
            }
            callCamera();
        }

        private void callCamera() {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getSecurityUri());
                intent.putExtra("return-data", false);
                startActivityForResult(intent, requestCodeCamera);
            } catch (Exception e) {
                callFailure(ERROR_INTENT);
            }
        }

        private void callSucceed() {
            if (onCameraListener != null) onCameraListener.onSucceed(file);
            removeSelf();
        }

        private void callFailure(int type) {
            if (onCameraListener != null) onCameraListener.onFailure(type);
            removeSelf();
        }

        private void addSelf(Activity activity) {
            FragmentManager manager = activity.getFragmentManager();
            manager
                    .beginTransaction()
                    .add(this, TAG)
                    .commitAllowingStateLoss();
            manager.executePendingTransactions();
        }

        private void removeSelf() {
            FragmentManager manager = getActivity().getFragmentManager();
            manager.beginTransaction()
                    .remove(this)
                    .commitAllowingStateLoss();
            manager.executePendingTransactions();
        }

        private Uri getSecurityUri() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return Uri.fromFile(file);
            }
            return FileProvider.getUriForFile(getContext(), AUTHORITY, file);
        }
    }

    public interface OnCameraListener {

        void onSucceed(File file);

        void onFailure(int type);
    }
}
