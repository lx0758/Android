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
import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.rule.SuperRule;
import com.liux.android.list.decoration.AbsItemDecoration;
import com.liux.android.list.holder.SuperHolder;
import com.liux.android.util.DateUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/11/28.
 */

public class ListActivity extends AppCompatActivity {

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    private MultipleAdapter<Bean> mMultipleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new AbsItemDecoration() {
            @Override
            public Decoration getItemOffsets(int position) {
                // 划重点
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
                .setHeader(LayoutInflater.from(this).inflate(R.layout.layout_header, rvList, false))
                .setFooter(LayoutInflater.from(this).inflate(R.layout.layout_footer, rvList, false))
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
                                bean.setSelected(!bean.isSelected());
                                mMultipleAdapter.notifyItemChanged(position, 0);
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
                                bean.setSelected(!bean.isSelected());
                                mMultipleAdapter.notifyItemChanged(position);
                            }
                        });
                    }
                });
        rvList.setAdapter(mMultipleAdapter);
    }

    @OnClick({R.id.btn_add_string, R.id.btn_add_long, R.id.btn_del_first, R.id.btn_4, R.id.btn_5, R.id.btn_6})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_string:
                mMultipleAdapter.getData().add(new Bean(DateUtil.date2string(new Date())));
                break;
            case R.id.btn_add_long:
                mMultipleAdapter.getData().add(new Bean(new Date().getTime()));
                break;
            case R.id.btn_del_first:
                if (mMultipleAdapter.getData().isEmpty()) return;
                mMultipleAdapter.getData().remove(0);
                break;
            case R.id.btn_4:
                break;
            case R.id.btn_5:
                break;
            case R.id.btn_6:
                break;
        }
        mMultipleAdapter.notifyDataSetChanged();
    }
}
