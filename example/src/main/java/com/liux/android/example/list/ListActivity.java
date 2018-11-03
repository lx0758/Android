package com.liux.android.example.list;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.liux.android.example.R;
import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.state.State;
import com.liux.android.list.adapter.state.SuperRule;
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

    private MultipleAdapter<Object> mMultipleAdapter;

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
                decoration.color = Color.parseColor("#FF00FF");
                decoration.bottom = 15;
                return decoration;
            }
        });
        mMultipleAdapter = new MultipleAdapter<Object>()
                .setHeader(LayoutInflater.from(this).inflate(R.layout.layout_header, rvList, false))
                .setFooter(LayoutInflater.from(this).inflate(R.layout.layout_footer, rvList, false))
                .addRule(new SuperRule<String>(android.R.layout.simple_list_item_1) {
                    @Override
                    public boolean doBindData(String string) {
                        return true;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, String s, State state, final int position) {
                        holder.setText(android.R.id.text1, String.format("String is %s\n(%s)", s, state));
                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMultipleAdapter.toggleSelect(position);
                            }
                        });
                    }
                })
                .addRule(new SuperRule<Long>(android.R.layout.simple_list_item_2) {
                    @Override
                    public boolean doBindData(Long l) {
                        return true;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, Long l, State state, final int position) {
                        holder.setText(android.R.id.text1, String.format("Long is %s\n(%s)", l.toString(), state));
                        holder.setText(android.R.id.text2, String.format("I'm a descriptive text %s", l.toString()));
                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMultipleAdapter.toggleSelect(position);
                            }
                        });
                    }
                });
        mMultipleAdapter.setOnSelectListener(new OnSelectListener<Object>() {
            @Override
            public boolean onSelectChange(Object o, int position, boolean isSelect) {
                TT.makeText(ListActivity.this, o + " requestRuntime select:" + isSelect, TT.LENGTH_SHORT).show();
                return position % 3 != 0;
            }

            @Override
            public void onSelectFailure() {
                TT.makeText(ListActivity.this, "select failure", TT.LENGTH_SHORT).show();
            }

            @Override
            public void onSelectComplete() {
                List<Object> list = mMultipleAdapter.getSelectedAll();
                TT.makeText(ListActivity.this, list.toString(), TT.LENGTH_SHORT).show();
            }
        });
        rvList.setAdapter(mMultipleAdapter);
    }

    @OnClick({R.id.btn_add_string, R.id.btn_add_long, R.id.btn_del_first, R.id.btn_open5, R.id.btn_set8, R.id.btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_string:
                mMultipleAdapter.getData().add(DateUtil.date2string(new Date(), "MM-dd HH:mm"));
                break;
            case R.id.btn_add_long:
                mMultipleAdapter.getData().add(new Date().getTime());
                break;
            case R.id.btn_del_first:
                if (mMultipleAdapter.getData().isEmpty()) return;
                mMultipleAdapter.getData().remove(0);
                break;
            case R.id.btn_open5:
                mMultipleAdapter.setEnabledSelect(true, 5);
                break;
            case R.id.btn_set8:
                break;
            case R.id.btn_close:
                mMultipleAdapter.setEnabledSelect(false);
                break;
        }
        mMultipleAdapter.notifyDataSetChanged();
    }
}
