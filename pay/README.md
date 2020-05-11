Pay
===

使用说明
---
```
implementation 'com.liux.android:pay:x.y.z'
```
新版本避免应用程序需要太多冗余的权限, 所以移除了权限清单, 需要开发者根据实际情况自行添加
```xml
    <!-- 微信 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <!-- for mta statistics -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

    <!-- 支付宝 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

    <!-- 银联 -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.NFC" />
    <uses-feature android:name="android.hardware.nfc.hce"/>
    <uses-permission android:name="org.simalliance.openmobileapi.SMARTCARD" />
```


更新说明
---
### x.y.z_202x-xx-xx
    1.

### 0.2.3_2020-05-11
    1.移除自带权限声明
    2.升级微信SDK版本(6.6.4)
    3.升级支付宝SDK版本(15.7.5)
    4.升级银联SDK版本(3.4.9)

### 0.2.2_2019-09-20
    1.升级微信SDK版本(5.4.3)
    2.升级银联SDK版本(3.4.8)

### 0.2.1_2019-09-19
    1.新增支付宝V2协议

### 0.2.0_2019-08-26
    1.支持库迁移至AndroidX
    2.升级支付宝&银联SDK

### 0.1.3_2019-01-08
    1.升级支付宝SDK

### 0.1.2_2018-10-31
    1.调整微信回调代码
    2.更改工具类名称

### 0.1.0_2018-10-14
    1.发布0.1.0版本