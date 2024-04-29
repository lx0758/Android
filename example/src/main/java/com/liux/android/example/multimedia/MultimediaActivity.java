package com.liux.android.example.multimedia;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.liux.android.example.R;
import com.liux.android.example.databinding.ActivityBoxingDemoBinding;
import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.rule.SingleRule;
import com.liux.android.list.decoration.GridItemDecoration;
import com.liux.android.list.holder.SuperHolder;
import com.liux.android.multimedia.Multimedia;
import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.glide.GlideApp;
import com.liux.android.multimedia.listener.OnMultiSelectListener;
import com.liux.android.multimedia.listener.OnRecordListener;
import com.liux.android.multimedia.listener.OnSingleSelectListener;
import com.liux.android.multimedia.listener.OnTakeListener;
import com.liux.android.multimedia.listener.OnVideoSelectListener;
import com.liux.android.tool.TT;

import java.util.List;

/**
 * Created by Liux on 2017/11/28.
 */

public class MultimediaActivity extends AppCompatActivity {

    private ActivityBoxingDemoBinding mViewBinding;

    private MultipleAdapter<Uri> mMultipleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityBoxingDemoBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.rvList.addItemDecoration(new GridItemDecoration(10, 3));
        mViewBinding.rvList.setLayoutManager(new GridLayoutManager(this, 3));
        mMultipleAdapter = new MultipleAdapter<Uri>()
                .addRule(new SingleRule<Uri>(R.layout.layout_media_item) {
                    @Override
                    public void onDataBind(SuperHolder holder, int position, Uri uri, List<Object> payloads) {
                        ImageView imageView = holder
                                .setOnClickListener(R.id.tv_del, v -> {
                                    int pos = mMultipleAdapter.getData().indexOf(uri);
                                    mMultipleAdapter.getData().remove(pos);
                                    mMultipleAdapter.notifyItemRemoved(pos);
                                })
                                .setOnClickListener(v -> {
                                    Multimedia
                                            .with(MultimediaActivity.this)
                                            .preview(mMultipleAdapter.getData())
                                            .position(mMultipleAdapter.getData().indexOf(uri))
                                            .start();
                                })
                                .getView(R.id.iv_image);
                        GlideApp.with(imageView.getContext()).asBitmap().load(uri).override(150, 150).into(imageView);
                    }
                });
        mViewBinding.rvList.setAdapter(mMultipleAdapter);

        mViewBinding.btnSelectPic.setOnClickListener(this::onViewClicked);
        mViewBinding.btnSelectPics.setOnClickListener(this::onViewClicked);
        mViewBinding.btnSelectVideo.setOnClickListener(this::onViewClicked);
        mViewBinding.btnTake.setOnClickListener(this::onViewClicked);
        mViewBinding.btnRecord.setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select_pic:
                Multimedia
                        .with(this)
                        .singleSelect()
                        .useCrop(true)
                        .listener(() -> TT.show("操作已取消"))
                        .listener(new OnSingleSelectListener() {
                            @Override
                            public void onFailure(MultimediaException e) {
                                switch (e.getType()) {
                                    case MultimediaException.TYPE_UNKNOWN:
                                        TT.show("未知错误");
                                        break;
                                    case MultimediaException.TYPE_INTENT:
                                        TT.show("没有找到对应程序");
                                        break;
                                    case MultimediaException.TYPE_PERMISSION:
                                        TT.show("权限不足");
                                        break;
                                }
                            }

                            @Override
                            public void onSingleSelect(Uri uri) {
                                mMultipleAdapter.getData().add(uri);
                                mMultipleAdapter.notifyItemInserted(mMultipleAdapter.getData().size() - 1);
                            }
                        })
                        .start();
                break;
            case R.id.btn_select_pics:
                Multimedia
                        .with(this)
                        .multipleSelect(5)
                        .listener(() -> TT.show("操作已取消"))
                        .listener(new OnMultiSelectListener() {
                            @Override
                            public void onFailure(MultimediaException e) {
                                switch (e.getType()) {
                                    case MultimediaException.TYPE_UNKNOWN:
                                        TT.show("未知错误");
                                        break;
                                    case MultimediaException.TYPE_INTENT:
                                        TT.show("没有找到对应程序");
                                        break;
                                    case MultimediaException.TYPE_PERMISSION:
                                        TT.show("权限不足");
                                        break;
                                }
                            }

                            @Override
                            public void onMultiSelect(List<Uri> uris) {
                                mMultipleAdapter.getData().addAll(uris);
                                mMultipleAdapter.notifyDataSetChanged();
                            }
                        })
                        .start();
                break;
            case R.id.btn_select_video:
                Multimedia
                        .with(this)
                        .videoSelect()
                        .listener(() -> TT.show("操作已取消"))
                        .listener(new OnVideoSelectListener() {
                            @Override
                            public void onFailure(MultimediaException e) {
                                switch (e.getType()) {
                                    case MultimediaException.TYPE_UNKNOWN:
                                        TT.show("未知错误");
                                        break;
                                    case MultimediaException.TYPE_INTENT:
                                        TT.show("没有找到对应程序");
                                        break;
                                    case MultimediaException.TYPE_PERMISSION:
                                        TT.show("权限不足");
                                        break;
                                }
                            }

                            @Override
                            public void onVideoSelect(Uri uri) {
                                mMultipleAdapter.getData().add(uri);
                                mMultipleAdapter.notifyItemInserted(mMultipleAdapter.getData().size() - 1);
                            }
                        })
                        .start();
                break;
            case R.id.btn_take:
                Multimedia
                        .with(MultimediaActivity.this)
                        .take()
                        .useCrop(true)
                        .listener(() -> TT.show("操作已取消"))
                        .listener(new OnTakeListener() {
                            @Override
                            public void onFailure(MultimediaException e) {
                                switch (e.getType()) {
                                    case MultimediaException.TYPE_UNKNOWN:
                                        TT.show("未知错误");
                                        break;
                                    case MultimediaException.TYPE_INTENT:
                                        TT.show("没有找到对应程序");
                                        break;
                                    case MultimediaException.TYPE_PERMISSION:
                                        TT.show("权限不足");
                                        break;
                                }
                            }

                            @Override
                            public void onSucceed(Uri uri) {
                                mMultipleAdapter.getData().add(uri);
                                mMultipleAdapter.notifyItemInserted(mMultipleAdapter.getData().size() - 1);
                            }
                        })
                        .start();
                break;
            case R.id.btn_record:
                Multimedia.with(this)
                        .record()
                        .duration(30)
                        .size(2 * 1024 * 1024)
                        .quality(0)
                        .listener(() -> TT.show("操作已取消"))
                        .listener(new OnRecordListener() {
                            @Override
                            public void onFailure(MultimediaException e) {
                                switch (e.getType()) {
                                    case MultimediaException.TYPE_UNKNOWN:
                                        TT.show("未知错误");
                                        break;
                                    case MultimediaException.TYPE_INTENT:
                                        TT.show("没有找到对应程序");
                                        break;
                                    case MultimediaException.TYPE_PERMISSION:
                                        TT.show("权限不足");
                                        break;
                                }
                            }

                            @Override
                            public void onSucceed(Uri uri) {
                                mMultipleAdapter.getData().add(uri);
                                mMultipleAdapter.notifyItemInserted(mMultipleAdapter.getData().size() - 1);
                            }
                        })
                        .start();
                break;
        }
    }
}
