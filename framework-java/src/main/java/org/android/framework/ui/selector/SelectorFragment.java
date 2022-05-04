package org.android.framework.ui.selector;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.rule.SingleRule;
import com.liux.android.list.holder.SuperHolder;

import org.android.framework.R;
import org.android.framework.util.PreventJitterUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * date：2018/11/26 16:27
 * author：Liux
 * email：lx0758@qq.com
 * description：
 */
public class SelectorFragment extends Fragment {

    private int level;
    private boolean showSearch;
    private List<? extends SelectorBean> selectorBeans;
    private OnSelectedListener onSelectedListener;

    private MultipleAdapter<SelectorBean> multipleAdapter;

    private EditText search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_selector_fragment, container, false);

        search = view.findViewById(R.id.et_search);
        search.setVisibility(showSearch ? View.VISIBLE : View.GONE);
        PreventJitterUtil.onTextChanged(search, new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                onShowData();
            }
        });

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

        RecyclerView recyclerView = view.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(multipleAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onShowData();
    }

    public void refresh(int level, boolean showSearch, List<? extends SelectorBean> selectorBeans, OnSelectedListener onSelectedListener) {
        this.level = level;
        this.showSearch = showSearch;
        this.selectorBeans = selectorBeans;
        this.onSelectedListener = onSelectedListener;
    }

    public int getLevel() {
        return level;
    }

    public List<? extends SelectorBean> getSelectorBeans() {
        return selectorBeans;
    }

    private void onShowData() {
        multipleAdapter.getData().clear();

        if (selectorBeans != null) {
            List<SelectorBean> beans = new ArrayList<>(selectorBeans);
            String keyword = search.getText().toString().trim();
            if (!TextUtils.isEmpty(keyword)) {
                for (Iterator<SelectorBean> it = beans.iterator(); it.hasNext(); ) {
                    SelectorBean selectorBean = it.next();
                    if (!selectorBean.getILabel().contains(keyword)) {
                        it.remove();
                    }
                }
            }
            multipleAdapter.getData().addAll(beans);
        }

        multipleAdapter.notifyDataSetChanged();
    }

    interface OnSelectedListener {

        void onSelected(int level, SelectorBean selectorBean);
    }
}
