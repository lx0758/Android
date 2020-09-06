package com.liux.android.example.list;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liux.android.example.R;
import com.liux.android.example.databinding.ActivityListBinding;
import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.rule.SuperRule;
import com.liux.android.list.decoration.AbsItemDecoration;
import com.liux.android.list.helper.SelectCallback;
import com.liux.android.list.helper.SelectHelper;
import com.liux.android.list.holder.SuperHolder;
import com.liux.android.tool.TT;
import com.liux.android.util.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Liux on 2017/11/28.
 */

public class ListActivity extends AppCompatActivity {

    private ActivityListBinding mViewBinding;

    private MultipleAdapter<Bean> mMultipleAdapter;
    private SelectHelper<Bean> mSelectHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.rvList.setLayoutManager(new LinearLayoutManager(this));
        mViewBinding.rvList.addItemDecoration(new AbsItemDecoration() {
            @Override
            public Decoration getItemOffsets(int position) {
                if (mMultipleAdapter.isHeaderPosition(position) ||
                        mMultipleAdapter.isFooterPosition(position)) {
                    return null;
                }
                ColorDecoration decoration = new ColorDecoration();
                decoration.color = Color.parseColor("#DDDDDD");
                decoration.bottom = 15;
                return decoration;
            }
        });
        mMultipleAdapter = new MultipleAdapter<Bean>()
                .setHeader(getLayoutInflater().inflate(R.layout.layout_header, mViewBinding.rvList, false))
                .setFooter(getLayoutInflater().inflate(R.layout.layout_footer, mViewBinding.rvList, false))
                .addRule(new SuperRule<Bean>(android.R.layout.simple_list_item_1) {
                    @Override
                    public boolean canBindData(Bean bean) {
                        return bean.getObject() instanceof String;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, int position, Bean bean, List<Object> payloads) {
                        holder.setText(android.R.id.text1, String.format("bean type is %s\nselected %s", bean.getObject().getClass().getSimpleName(), bean.isSelected()));
                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSelectHelper.toggleSelect(bean);
                            }
                        });
                    }
                })
                .addRule(new SuperRule<Bean>(android.R.layout.simple_list_item_2) {
                    @Override
                    public boolean canBindData(Bean bean) {
                        return bean.getObject() instanceof Long;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, int position, Bean bean, List<Object> payloads) {
                        holder.setText(android.R.id.text1, String.format("bean type is %s\nselected %s", bean.getObject().getClass().getSimpleName(), bean.isSelected()));
                        holder.setText(android.R.id.text2, "I'm a descriptive text");
                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSelectHelper.toggleSelect(bean);
                            }
                        });
                    }
                });
        mViewBinding.rvList.setAdapter(mMultipleAdapter);

        mSelectHelper = new SelectHelper<>(mMultipleAdapter, 10);
        mSelectHelper.setSelectCallback(new SelectCallback<Bean>() {
            @Override
            public boolean onSelectBefore(Bean bean) {
                return true;
            }

            @Override
            public void onSelect(Bean bean, boolean selected) {
                TT.show("onSelect(pos:" + mMultipleAdapter.getData().indexOf(bean) + ", selected is:" + selected + ")");
            }

            @Override
            public void onSelectFailure(int type) {
                TT.show("onSelectFailure(type:" + type + ")");
            }

            @Override
            public void onSelectFull() {
                TT.show("onSelectFull()");
            }
        });

        mViewBinding.btnAddString.setOnClickListener(view -> {
            mMultipleAdapter.getData().add(new Bean(DateUtil.date2string(new Date())));
            mMultipleAdapter.notifyItemInserted(mMultipleAdapter.getAdapterPosition(mMultipleAdapter.getData().size() - 1));
        });
        mViewBinding.btnAddLong.setOnClickListener(view -> {
            mMultipleAdapter.getData().add(new Bean(new Date().getTime()));
            mMultipleAdapter.notifyItemInserted(mMultipleAdapter.getAdapterPosition(mMultipleAdapter.getData().size() - 1));
        });
        mViewBinding.btnDelFirst.setOnClickListener(view -> {
            if (mMultipleAdapter.getData().isEmpty()) return;
            mMultipleAdapter.getData().remove(0);
            mMultipleAdapter.notifyItemRemoved(mMultipleAdapter.getAdapterPosition(0));
        });
        mViewBinding.btn4.setOnClickListener(view -> {

        });
        mViewBinding.btn5.setOnClickListener(view -> {

        });
        mViewBinding.btn6.setOnClickListener(view -> {

        });
    }
}
