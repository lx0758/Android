package com.liux.android.util.extra;

import androidx.core.content.FileProvider;

/**
 * 用于适配Android 7.0 的 FileProvider
 * 继承之后可以解决一个项目存在多个FileProvider的情况
 */
public class ExtraFileProvider extends FileProvider {
}
