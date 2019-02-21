package com.liux.android.example.boxing;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.model.entity.impl.VideoMedia;
import com.liux.android.boxing.BoxingGlideLoader;
import com.liux.android.boxing.Boxinger;
import com.liux.android.boxing.BoxingUcrop;
import com.liux.android.boxing.OnCancelListener;
import com.liux.android.boxing.OnCropListener;
import com.liux.android.boxing.OnMultiSelectListener;
import com.liux.android.boxing.OnRecordListener;
import com.liux.android.boxing.OnSingleSelectListener;
import com.liux.android.boxing.OnTakeListener;
import com.liux.android.boxing.OnVideoSelectListener;
import com.liux.android.example.R;
import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.state.State;
import com.liux.android.list.adapter.state.SuperRule;
import com.liux.android.list.decoration.GridItemDecoration;
import com.liux.android.list.holder.SuperHolder;
import com.liux.android.tool.TT;
import com.liux.android.util.UriUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/11/28.
 */

public class BoxingActivity extends AppCompatActivity {

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    private MultipleAdapter<String> mMultipleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_boxing_demo);
        ButterKnife.bind(this);


        rvList.addItemDecoration(new GridItemDecoration(10, 3));
        rvList.setLayoutManager(new GridLayoutManager(this, 3));
        mMultipleAdapter = new MultipleAdapter<String>()
                .addRule(new SuperRule<String>(R.layout.layout_media_item) {
                    @Override
                    public boolean doBindData(String string) {
                        return true;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, String path, State state, final int position) {
                        ImageView imageView = holder.getView(R.id.iv_image);
                        BoxingMediaLoader.getInstance().displayThumbnail(imageView, path, 150, 150);
                        // GlideApp.with(imageView.getContext()).asBitmap().load(path).override(150, 150).into(imageView);

                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] medias = new String[mMultipleAdapter.getData().size()];
                                mMultipleAdapter.getData().toArray(medias);
                                Boxinger
                                        .with(BoxingActivity.this)
                                        .preview(medias)
                                        .position(position)
                                        .start();
                            }
                        });
                    }
                });
        rvList.setAdapter(mMultipleAdapter);

        Boxinger.init();
    }

    @OnClick({R.id.btn_select_pic, R.id.btn_select_pic_clip, R.id.btn_select_pics, R.id.btn_select_video,R.id.btn_take_and_crop,R.id.btn_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select_pic:
                Boxinger
                        .with(this)
                        .singleSelect()
                        .useCamera(true)
                        .useCrop(false)
                        .listener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .listener(new OnSingleSelectListener() {
                            @Override
                            public void onSingleSelect(ImageMedia imageMedia) {
                                mMultipleAdapter.getData().clear();
                                mMultipleAdapter.getData().add(imageMedia.getPath());
                                mMultipleAdapter.notifyDataSetChanged();
                            }
                        })
                        .start();
                break;
            case R.id.btn_select_pic_clip:
                Boxinger
                        .with(this)
                        .singleSelect()
                        .useCamera(true)
                        .useCrop(true)
                        .listener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .listener(new OnSingleSelectListener() {
                            @Override
                            public void onSingleSelect(ImageMedia imageMedia) {
                                mMultipleAdapter.getData().clear();
                                mMultipleAdapter.getData().add(imageMedia.getPath());
                                mMultipleAdapter.notifyDataSetChanged();
                            }
                        })
                        .start();
                break;
            case R.id.btn_select_pics:
                Boxinger
                        .with(this)
                        .multipleSelect(5)
                        .useCamera(true)
                        .listener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .listener(new OnMultiSelectListener() {
                            @Override
                            public void onMultiSelect(List<ImageMedia> imageMedias) {
                                mMultipleAdapter.getData().clear();
                                for (BaseMedia media : imageMedias) {
                                    mMultipleAdapter.getData().add(media.getPath());
                                }
                                mMultipleAdapter.notifyDataSetChanged();
                            }
                        })
                        .start();
                break;
            case R.id.btn_select_video:
                Boxinger
                        .with(this)
                        .videoSelect()
                        .listener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .listener(new OnVideoSelectListener() {
                            @Override
                            public void onVideoSelect(VideoMedia videoMedia) {
                                mMultipleAdapter.getData().clear();
                                mMultipleAdapter.getData().add(videoMedia.getPath());
                                mMultipleAdapter.notifyDataSetChanged();
                            }
                        })
                        .start();
                break;
            case R.id.btn_take_and_crop:
                Boxinger
                        .with(BoxingActivity.this)
                        .take(UriUtil.getAuthority(this))
                        .listener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .listener(new OnTakeListener() {
                            @Override
                            public void onSucceed(Uri uri) {
                                Boxinger
                                        .with(BoxingActivity.this)
                                        .crop(uri)
                                        .listener(new OnCropListener() {
                                            @Override
                                            public void onSucceed(Uri output) {
                                                mMultipleAdapter.getData().clear();
                                                mMultipleAdapter.getData().add(output.toString());
                                                mMultipleAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onFailure() {
                                                TT.show("裁剪失败");
                                            }
                                        })
                                        .start();
                            }

                            @Override
                            public void onFailure(int type) {
                                switch (type) {
                                    case OnTakeListener.ERROR_INTENT:
                                        TT.show("没有找到相机程序");
                                        break;
                                    case OnTakeListener.ERROR_PERMISSION:
                                        TT.show("没有授权使用相机权限");
                                        break;
                                }
                            }
                        })
                        .start();
                break;
            case R.id.btn_record:
                Boxinger.with(this)
                        .record(UriUtil.getAuthority(this))
                        .duration(30)
                        .size(2 * 1024 * 1024)
                        .quality(0)
                        .listener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .listener(new OnRecordListener() {
                            @Override
                            public void onSucceed(Uri uri) {
                                mMultipleAdapter.getData().clear();
                                mMultipleAdapter.getData().add(uri.toString());
                                mMultipleAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(int type) {
                                switch (type) {
                                    case OnRecordListener.ERROR_INTENT:
                                        TT.show("没有找到相机程序");
                                        break;
                                    case OnRecordListener.ERROR_PERMISSION:
                                        TT.show("没有授权使用相机权限");
                                        break;
                                }
                            }
                        })
                        .start();
                break;
        }
    }
}
