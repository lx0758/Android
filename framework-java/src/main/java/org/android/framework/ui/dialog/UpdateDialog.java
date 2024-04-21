package org.android.framework.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;

import org.android.framework.R;
import com.liux.android.http.Http;
import com.liux.android.http.request.DownloadCallback;
import com.liux.android.http.request.RequestManager;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class UpdateDialog extends AppCompatDialog {
    private static final int STATE_PREPARE = 0;
    private static final int STATE_DOWNLOADING = 1;
    private static final int STATE_DOWNLOADED = 2;
    private static final int STATE_FAILURE = 3;

    private Builder builder;

    private View viDividing, llProgress;
    private TextView tvTitle, tvContent, tvOption, tvCancel, tvProgress;
    private ProgressBar pbDownload;

    private int updateState = STATE_PREPARE;
    private File downloadFile;
    private RequestManager requestManager = RequestManager.Builder.build();

    public static Builder with(Context context, Callback callback) {
        return new Builder(context, callback);
    }

    private UpdateDialog(Builder builder) {
        super(builder.context);
        this.builder = builder;
        getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(R.layout.dialog_update);

        viDividing = findViewById(R.id.vi_dividing);
        llProgress = findViewById(R.id.ll_progress);
        tvTitle = findViewById(R.id.tv_titile);
        tvContent = findViewById(R.id.tv_content);
        tvOption = findViewById(R.id.tv_option);
        tvCancel = findViewById(R.id.tv_cancel);
        tvProgress = findViewById(R.id.tv_progress);
        pbDownload = findViewById(R.id.pb_download);

        Drawable[] drawables = new Drawable[3];
        drawables[0] = new ColorDrawable(0x30000000);
        drawables[1] = new ClipDrawable(
                new ColorDrawable(0x30000000),
                Gravity.LEFT,
                ClipDrawable.HORIZONTAL
        );
        drawables[2] = new ClipDrawable(
                new ColorDrawable(builder.progressColor),
                Gravity.LEFT,
                ClipDrawable.HORIZONTAL
        );
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        layerDrawable.setId(0, android.R.id.background);
        layerDrawable.setId(1, android.R.id.secondaryProgress);
        layerDrawable.setId(2, android.R.id.progress);
        pbDownload.setProgressDrawable(layerDrawable);

        if (!TextUtils.isEmpty(builder.title)) tvTitle.setText(builder.title);

        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(builder.content)) {
            stringBuilder.append("更新内容:").append("\n").append(builder.content).append("\n\n");
        }
        if (!TextUtils.isEmpty(builder.date)) {
            stringBuilder.append("更新时间:").append("\n").append(builder.date).append("\n");
        }
        if (builder.size != 0) {
            stringBuilder.append("安装包:").append("\n").append(String.format(Locale.getDefault(), "%.2fMB", (builder.size / 1024.0F / 1024.0F))).append("\n");
        }
        if (!TextUtils.isEmpty(builder.version)) {
            stringBuilder.append("版本号:").append("\n").append(builder.version).append("\n");
        }
        while (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == '\n') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        tvContent.setText(stringBuilder.toString());

        if (builder.force) {
            viDividing.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
            setCancelable(false);
        }

        tvOption.setOnClickListener(v -> {
            switch (updateState) {
                case STATE_PREPARE:
                    startDownload();
                    break;
                case STATE_DOWNLOADING:
                    if (builder.force) break;
                    cancelUpdate();
                    break;
                case STATE_DOWNLOADED:
                    startInstall();
                    break;
                case STATE_FAILURE:
                    startDownload();
                    break;
            }
        });
        tvCancel.setOnClickListener(v -> {
            cancelUpdate();
        });

        setOnDismissListener(dialog -> cancelUpdate());
    }

    private void startDownload() {
        setCancelable(false);
        tvOption.setText(builder.force ? "下载中..." : "取消更新");
        viDividing.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);
        llProgress.setVisibility(View.VISIBLE);
        updateState = STATE_DOWNLOADING;

        File dir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            dir = getContext().getExternalCacheDir();
        else
            dir = getContext().getCacheDir();
        Http.get().get(builder.url)
                .addQuery("t", String.valueOf(System.currentTimeMillis()))
                .manager(requestManager)
                .download(
                        new File(dir, System.currentTimeMillis() + ".apk"),
                        new DownloadCallback() {
                            @Override
                            public void onProgress(long totalBytesRead, long contentLength) {
                                updateProgress(contentLength, totalBytesRead);
                                pbDownload.setProgress((int) (totalBytesRead * 100 / contentLength));
                            }

                            @Override
                            public void onSucceed(File file) {
                                downloadFile = file;
                                tvOption.setText("开始安装");
                                pbDownload.setProgress(100);
                                updateState = STATE_DOWNLOADED;
                                startInstall();
                            }

                            @Override
                            public void onFailure(IOException e) {
                                Toast.makeText(getContext(), "下载失败", Toast.LENGTH_LONG).show();
                                setCancelable(true);
                                tvOption.setText("重试");
                                viDividing.setVisibility(View.VISIBLE);
                                tvCancel.setVisibility(View.VISIBLE);
                                updateState = STATE_FAILURE;
                            }
                        }
                );
    }

    private void cancelUpdate() {
        requestManager.cancelAll();
        dismiss();
    }

    private void startInstall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !getContext().getPackageManager().canRequestPackageInstalls()) {
            Toast.makeText(getContext(), "请允许安装应用程序", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.fromParts("package", getContext().getPackageName(), null));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContext().startActivity(intent);
            return;
        }

        Uri uri = builder.callback.onTransform(downloadFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getContext().startActivity(intent);
    }

    private void updateProgress(long totalSize, long currentSize) {
        if (totalSize < 0) totalSize = 0;
        if (currentSize < 0) currentSize = 0;
        if (totalSize > 0) {
            pbDownload.setProgress((int) (currentSize * 100 / totalSize));
        } else {
            pbDownload.setProgress(0);
        }
        tvProgress.setText(String.format(Locale.getDefault(), "%.1fMb/%.1fMb", currentSize / 1024.0 / 1024.0, totalSize / 1024.0 / 1024.0));
    }

    public static class Builder {

        Context context;

        String url;
        boolean force;

        String title, content, version, date, extra;
        long size;
        int progressColor = 0xFF008FFF;

        Callback callback;

        public Builder(Context context, Callback callback) {
            this.context = context;
            this.callback = callback;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder force(boolean force) {
            this.force = force;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder size(long size) {
            this.size = size;
            return this;
        }

        public Builder extra(String extra) {
            this.extra = extra;
            return this;
        }

        public Builder progressColor(int progressColor) {
            this.progressColor = progressColor;
            return this;
        }

        public UpdateDialog build() {
            return new UpdateDialog(this);
        }
    }

    public interface Callback {

        Uri onTransform(File localFile);
    }
}
