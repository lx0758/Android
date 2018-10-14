package com.liux.android.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.liux.android.example.banner.BannerActivity;
import com.liux.android.example.abstracts.AbstractsActivity;
import com.liux.android.example.boxing.BoxingActivity;
import com.liux.android.example.glide.GlideActivity;
import com.liux.android.example.http.HTTPActivity;
import com.liux.android.example.list.ListActivity;
import com.liux.android.example.other.ToolActivity;
import com.liux.android.example.pay.PayActivity;
import com.liux.android.example.permission.PermissionActivity;
import com.liux.android.example.qrcode.QRCodeActivity;
import com.liux.android.example.rx.RXActivity;
import com.liux.android.example.util.UtilActivity;
import com.liux.android.example.view.ViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<Map<String, Object>> mDataSource;
    private SimpleAdapter mSimpleAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDataSource = new ArrayList<>();
        mSimpleAdapter = new SimpleAdapter(
                this,
                mDataSource,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "describe"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        mListView = (ListView) findViewById(R.id.lv_list);
        mListView.setAdapter(mSimpleAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class activity = (Class) mDataSource.get(position).get("activity");
                startActivity(new Intent(MainActivity.this, activity));
            }
        });

        add("Banner", "一个支持Adapter的Banner封装", BannerActivity.class);
        add("Abstracts", "对于Activity和Fragment的封装", AbstractsActivity.class);
        add("Boxing", "基于Boxing封装的图片选择/预览库", BoxingActivity.class);
        //add("Downloader", "全局/多线程/断点续传下载器实现", DownloaderActivity.class);
        add("Glide", "基于Glide4实现自定义加载过程", GlideActivity.class);
        add("HTTP", "基于Retorfit/OkHttp的封装", HTTPActivity.class);
        add("List", "基于RecycleView的封装", ListActivity.class);
        add("Pay", "封装支付宝/微信/银联支付过程", PayActivity.class);
        add("Permission", "运行时权限申请过程封装", PermissionActivity.class);
        //add("Player", "基于ijkplayer封装的播放器", PlayerActivity.class);
        add("QRCode", "基于ZXing的一个二维码扫描/生成库", QRCodeActivity.class);
        add("RX", "基于ReactiveX的RxJava2全家桶部分成员", RXActivity.class);
        add("Tool", "一些工具类集合", ToolActivity.class);
        add("Util", "一些工具方法集合", UtilActivity.class);
        add("View", "一些自定义View集合", ViewActivity.class);

        mSimpleAdapter.notifyDataSetChanged();
    }

    private void add(String title, String describe, Class<? extends Activity> activity) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("describe", describe);
        map.put("activity", activity);
        mDataSource.add(map);
    }
}
