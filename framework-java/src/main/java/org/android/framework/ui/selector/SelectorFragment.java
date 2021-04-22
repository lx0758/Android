package org.android.framework.ui.selector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.rule.SingleRule;
import com.liux.android.list.holder.SuperHolder;

import java.util.List;

/**
 * date：2018/11/26 16:27
 * author：Liux
 * email：lx0758@qq.com
 * description：
 */
public class SelectorFragment extends Fragment {

    private int level;
    private List<? extends SelectorBean> selectorBeans;
    private OnSelectedListener onSelectedListener;

    private MultipleAdapter<SelectorBean> multipleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        multipleAdapter = new MultipleAdapter<SelectorBean>().addRule(new SingleRule<SelectorBean>(android.R.layout.simple_list_item_1) {
            @Override
            public void onDataBind(SuperHolder holder, int position, final SelectorBean selectorBean, List<Object> payloads) {
                holder
                        .setText(android.R.id.text1, selectorBean.getILabel())
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onSelectedListener.onSelected(level, selectorBean);
                            }
                        });
            }
        });
        RecyclerView recyclerView = new RecyclerView(inflater.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(multipleAdapter);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (selectorBeans != null) {
            multipleAdapter.getData().clear();
            multipleAdapter.getData().addAll(selectorBeans);
            multipleAdapter.notifyDataSetChanged();
        }
    }

    public void refresh(int level, List<? extends SelectorBean> selectorBeans, OnSelectedListener onSelectedListener) {
        this.level = level;
        this.selectorBeans = selectorBeans;
        this.onSelectedListener = onSelectedListener;
    }

    public int getLevel() {
        return level;
    }

    public List<? extends SelectorBean> getSelectorBeans() {
        return selectorBeans;
    }

    interface OnSelectedListener {

        void onSelected(int level, SelectorBean selectorBean);
    }
}
