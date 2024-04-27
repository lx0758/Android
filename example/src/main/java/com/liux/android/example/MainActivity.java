package com.liux.android.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.abstracts.activity.AbstractsActivity;
import com.liux.android.example.banner.BannerActivity;
import com.liux.android.example.downloader.DownloaderActivity;
import com.liux.android.example.http.HTTPActivity;
import com.liux.android.example.io.IOActivity;
import com.liux.android.example.list.ListActivity;
import com.liux.android.example.other.ToolActivity;
import com.liux.android.example.pay.PayActivity;
import com.liux.android.example.permission.PermissionActivity;
import com.liux.android.example.multimedia.MultimediaActivity;
import com.liux.android.example.qrcode.QRCodeActivity;
import com.liux.android.example.service.SMActivity;
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

        add("Banner", "一个支持 Adapter 的 Banner 封装", BannerActivity.class);
        add("Abstracts", "对于 Activity 和 Fragment 的封装", AbstractsActivity.class);
        add("Downloader", "全局/单线程/多任务/断点续传 HTTP 下载器", DownloaderActivity.class);
        add("HTTP", "基于 Retorfit/OkHttp 的封装", HTTPActivity.class);
        add("IO", "基于 Android中Linux内核 的 串口/GPIO/I2C 通信库类的封装", IOActivity.class);
        add("List", "基于 RecycleView 的封装", ListActivity.class);
        add("Multimedia", "多媒体文件选取/拍摄/录制/加载的库类", MultimediaActivity.class);
        add("Pay", "封装支付宝/微信/银联支付过程", PayActivity.class);
        add("Permission", "运行时/悬浮窗/安装 权限申请过程封装", PermissionActivity.class);
        add("QRCode", "基于 ZXing 的一个 QRCode 扫描/生成库", QRCodeActivity.class);
        add("Tool", "一些工具类集合", ToolActivity.class);
        add("SM", "服务治理框架", SMActivity.class);
        add("Util", "一些工具方法集合", UtilActivity.class);
        add("View", "一些自定义 View 集合", ViewActivity.class);

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
