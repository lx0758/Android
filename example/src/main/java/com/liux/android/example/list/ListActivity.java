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
import com.liux.android.list.adapter.Payload;
import com.liux.android.list.adapter.rule.SuperRule;
import com.liux.android.list.adapter.state.State;
import com.liux.android.list.decoration.AbsItemDecoration;
import com.liux.android.list.holder.SuperHolder;
import com.liux.android.list.listener.OnSelectListener;
import com.liux.android.util.DateUtil;
import com.liux.android.tool.TT;

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
                    public void onDataBind(SuperHolder holder, int position, Bean bean, List<Object> payloads, State state) {
                        if (!payloads.isEmpty()) {
                            for (Object payload : payloads) {
                                if (payload == Payload.STATE) {
                                    holder.setText(android.R.id.text1, String.format("bean is %s\ntype is %s\nstate is %s", bean, bean.getObject().getClass().getSimpleName(), state.getState("key")));
                                }
                            }
                            return;
                        }
                        holder.setText(android.R.id.text1, String.format("bean is %s\ntype is %s\nstate is %s", bean, bean.getObject().getClass().getSimpleName(), state.getState("key")));
                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                state.putState("key", System.currentTimeMillis(), true);
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
                    public void onDataBind(SuperHolder holder, int position, Bean bean, List<Object> payloads, State state) {
                        if (!payloads.isEmpty()) {
                            for (Object payload : payloads) {
                                if (payload == Payload.STATE) {
                                    holder.setText(android.R.id.text1, String.format("bean is %s\ntype is %s\nstate is %s", bean, bean.getObject().getClass().getSimpleName(), state.getState("key")));
                                }
                            }
                            return;
                        }
                        holder.setText(android.R.id.text1, String.format("bean is %s\ntype is %s\nstate is %s", bean, bean.getObject().getClass().getSimpleName(), state.getState("key")));
                        holder.setText(android.R.id.text2, "I'm a descriptive text");
                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                state.putState("key", System.currentTimeMillis(), true);
                            }
                        });
                    }
                });
        rvList.setAdapter(mMultipleAdapter);
    }

    @OnClick({R.id.btn_add_string, R.id.btn_add_long, R.id.btn_del_first, R.id.btn_open5, R.id.btn_set8, R.id.btn_close})
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
            case R.id.btn_open5:
                break;
            case R.id.btn_set8:
                break;
            case R.id.btn_close:
                break;
        }
        mMultipleAdapter.notifyDataSetChanged();
    }
}
