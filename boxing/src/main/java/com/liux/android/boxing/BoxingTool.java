package com.liux.android.boxing;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.config.BoxingCropOption;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.model.entity.impl.VideoMedia;
import com.bilibili.boxing.utils.BoxingFileHelper;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.bilibili.boxing_impl.ui.BoxingPreviewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Liux on 2017/11/13.
 */

public class BoxingTool {
    private static String CACHE_DIR;
    private static Map<BoxingFragment, Object> BOXINGS = new HashMap<>();

    public static boolean startSingle(Fragment fragment, boolean camera, boolean crop, OnSingleSelectListener listener) {
        return startSingle(fragment.getActivity(), camera, crop, listener);
    }

    public static boolean startSingle(android.support.v4.app.Fragment fragment, boolean camera, boolean crop, OnSingleSelectListener listener) {
        return startSingle(fragment.getActivity(), camera, crop, listener);
    }

    /**
     * 单张图片选取
     * @param activity
     * @param camera
     * @param crop
     * @param listener
     * @return
     */
    public static boolean startSingle(Activity activity, boolean camera, boolean crop, OnSingleSelectListener listener) {
        if (!checkCacheDir(activity)) return false;
        BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
                .withMediaPlaceHolderRes(R.drawable.ic_boxing_default_image)
                .needGif();
        if (camera) {
            config.needCamera(R.drawable.ic_boxing_camera_white);
        }
        if (crop) {
            config.withCropOption(new BoxingCropOption(new Uri.Builder()
                    .scheme("file")
                    .appendPath(CACHE_DIR)
                    .appendPath(String.format(Locale.US, "%s.png", System.currentTimeMillis()))
                    .build()));
        }
        start(activity, config, listener);
        return true;
    }

    public static boolean startMulti(Fragment fragment, int count, boolean camera, OnMultiSelectListener listener) {
        return startMulti(fragment.getActivity(), count, camera, listener);
    }

    public static boolean startMulti(android.support.v4.app.Fragment fragment, int count, boolean camera, OnMultiSelectListener listener) {
        return startMulti(fragment.getActivity(), count, camera, listener);
    }

    /**
     * 多张图片选取
     * @param activity
     * @param count
     * @param camera
     * @param listener
     * @return
     */
    public static boolean startMulti(Activity activity, int count, boolean camera, OnMultiSelectListener listener) {
        if (!checkCacheDir(activity)) return false;
        BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG)
                .withMediaPlaceHolderRes(R.drawable.ic_boxing_default_image)
                .withMaxCount(count)
                .needGif();
        if (camera) {
            config.needCamera(R.drawable.ic_boxing_camera_white);
        }
        start(activity, config, listener);
        return true;
    }

    public static boolean startVideo(Fragment fragment, OnVideoSelectListener listener) {
        return startVideo(fragment.getActivity(), listener);
    }

    public static boolean startVideo(android.support.v4.app.Fragment fragment, OnVideoSelectListener listener) {
        return startVideo(fragment.getActivity(), listener);
    }

    /**
     * 单个视频选取
     * @param activity
     * @param listener
     * @return
     */
    public static boolean startVideo(Activity activity, OnVideoSelectListener listener) {
        if (!checkCacheDir(activity)) return false;
        BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.VIDEO)
                .withVideoDurationRes(R.drawable.ic_boxing_play);
        start(activity, config, listener);
        return true;
    }

    /**
     * 开始转跳图片选择页面
     * @param activity
     * @param config
     * @param listener
     */
    private static void start(Activity activity, BoxingConfig config, Object listener) {
        BoxingFragment fragment = new BoxingFragment();
        int requestCode = (int) (System.currentTimeMillis() & 0xFFFF);

        FragmentManager manager = activity.getFragmentManager();
        manager
                .beginTransaction()
                .add(fragment, "BoxingTool")
                .commitAllowingStateLoss();
        manager.executePendingTransactions();
        Boxing.of(config).withIntent(fragment.getActivity(), BoxingActivity.class).start(fragment, requestCode);
        BOXINGS.put(fragment, listener);
    }

    public static void startPreview(Context context, String[] medias, int pos) {
        ArrayList<BaseMedia> media_list = new ArrayList<>();
        for (String path : medias) {
            media_list.add(new ImageMedia(String.valueOf(System.currentTimeMillis()), path));
        }
        startPreview(context, media_list, pos);
    }

    /**
     * 开始预览
     * @param context
     * @param medias
     * @param pos
     */
    public static void startPreview(Context context, ArrayList<BaseMedia> medias, int pos) {
        Intent intent = new Intent(context, BoxingPreviewActivity.class);
        intent.putParcelableArrayListExtra(BoxingPreviewActivity.PARAM_IMAGES, medias);
        intent.putExtra(BoxingPreviewActivity.PARAM_POS, pos);
        context.startActivity(intent);
    }

    /**
     * 处理Fragment返回的回调
     * @param fragment
     * @param resultCode
     * @param data
     */
    private static void result(BoxingFragment fragment, int resultCode, Intent data) {
        Object object = BOXINGS.get(fragment);
        BOXINGS.remove(fragment);

        FragmentManager manager = fragment.getActivity().getFragmentManager();
        manager.beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();

        if (resultCode != Activity.RESULT_OK) return;
        List<BaseMedia> medias = Boxing.getResult(data);

        if (object instanceof OnSingleSelectListener) {
            ((OnSingleSelectListener) object).onSingleSelect((ImageMedia) medias.get(0));
        } else if (object instanceof OnMultiSelectListener) {
            List<ImageMedia> imageMedias = new ArrayList<>();
            for (BaseMedia media : medias) {
                imageMedias.add((ImageMedia) media);
            }
            ((OnMultiSelectListener) object).onMultiSelect(imageMedias);
        } else if (object instanceof OnVideoSelectListener) {
            ((OnVideoSelectListener) object).onVideoSelect((VideoMedia) medias.get(0));
        }
    }

    /**
     * 检查缓存目录是否存在
     * @param context
     * @return
     */
    private static boolean checkCacheDir(Context context) {
        CACHE_DIR = null;
        context = context.getApplicationContext();
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        if (cacheDir == null) {
            return false;
        }

        String result = cacheDir.getAbsolutePath() + "/boxing";
        try {
            BoxingFileHelper.createFile(result);
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
        CACHE_DIR = result;
        return true;
    }

    public static class BoxingFragment extends Fragment {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            result(this, resultCode, data);
        }
    }
}
