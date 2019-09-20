/*
 *  Copyright (C) 2017 Bilibili
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.liux.android.boxing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.bilibili.boxing.loader.IBoxingCrop;
import com.bilibili.boxing.model.config.BoxingCropOption;
import com.yalantis.ucrop.UCrop;

/**
 * use Ucrop(https://github.com/Yalantis/uCrop) as the implement for {@link IBoxingCrop}
 *
 * @author ChenSL
 */
public class BoxingUcrop implements IBoxingCrop {

    @Override
    public void onStartCrop(Context context, Fragment fragment, @NonNull BoxingCropOption cropConfig,
                            @NonNull String path, int requestCode) {
        Uri uri = new Uri.Builder()
                .scheme("file")
                .appendPath(path)
                .build();

        UCrop.Options options = getOptions(context, cropConfig.getMaxWidth(), cropConfig.getMaxHeight(), cropConfig.getAspectRatioX(), cropConfig.getAspectRatioY());

        UCrop.of(uri, cropConfig.getDestination())
                .withOptions(options)
                .start(context, fragment, requestCode);
    }

    @Override
    public Uri onCropFinish(int resultCode, Intent data) {
        if (data == null) {
            return null;
        }
        Throwable throwable = UCrop.getError(data);
        if (throwable != null) {
            return null;
        }
        return UCrop.getOutput(data);
    }

    public static UCrop.Options getOptions(Context context, int maxWidth, int maxHeight, float aspectRatioX, float aspectRatioY) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(70);
        options.withMaxResultSize(maxWidth, maxHeight);
        options.withAspectRatio(aspectRatioX, aspectRatioY);

        options.setStatusBarColor(context.getResources().getColor(R.color.boxing_colorPrimaryDark));
        options.setToolbarColor(context.getResources().getColor(R.color.boxing_colorPrimaryDark));
        options.setToolbarWidgetColor(context.getResources().getColor(R.color.boxing_white));
        options.setActiveWidgetColor(context.getResources().getColor(R.color.boxing_colorPrimaryDark));

        return options;
    }
}
