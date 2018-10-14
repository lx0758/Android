package com.liux.android.example.boxing;

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
import com.liux.android.boxing.BoxingTool;
import com.liux.android.boxing.BoxingUcrop;
import com.liux.android.boxing.OnMultiSelectListener;
import com.liux.android.boxing.OnSingleSelectListener;
import com.liux.android.boxing.OnVideoSelectListener;
import com.liux.android.example.R;
import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.state.State;
import com.liux.android.list.adapter.state.SuperRule;
import com.liux.android.list.decoration.GridItemDecoration;
import com.liux.android.list.holder.SuperHolder;

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
                                BoxingTool.startPreview(BoxingActivity.this, medias, position);
                            }
                        });
                    }
                });
        rvList.setAdapter(mMultipleAdapter);

        /* 初始化Boxing */
        BoxingCrop.getInstance().init(new BoxingUcrop());
        BoxingMediaLoader.getInstance().init(new BoxingGlideLoader());
    }

    @OnClick({R.id.btn_select_pic, R.id.btn_select_pic_clip, R.id.btn_select_pics, R.id.btn_select_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select_pic:
                BoxingTool.startSingle(this, true, false, new OnSingleSelectListener() {
                    @Override
                    public void onSingleSelect(ImageMedia imageMedia) {
                        mMultipleAdapter.getData().clear();
                        mMultipleAdapter.getData().add(imageMedia.getPath());
                        mMultipleAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.btn_select_pic_clip:
                BoxingTool.startSingle(this, true, true, new OnSingleSelectListener() {
                    @Override
                    public void onSingleSelect(ImageMedia imageMedia) {
                        mMultipleAdapter.getData().clear();
                        mMultipleAdapter.getData().add(imageMedia.getPath());
                        mMultipleAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.btn_select_pics:
                BoxingTool.startMulti(this, 5, true, new OnMultiSelectListener() {
                    @Override
                    public void onMultiSelect(List<ImageMedia> imageMedias) {
                        mMultipleAdapter.getData().clear();
                        for (BaseMedia media : imageMedias) {
                            mMultipleAdapter.getData().add(media.getPath());
                        }
                        mMultipleAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.btn_select_video:
                BoxingTool.startVideo(this, new OnVideoSelectListener() {
                    @Override
                    public void onVideoSelect(VideoMedia videoMedia) {
                        mMultipleAdapter.getData().clear();
                        mMultipleAdapter.getData().add(videoMedia.getPath());
                        mMultipleAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }
}
