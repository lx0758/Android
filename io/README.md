IO
===
使用 [Google android-serialport-api](https://code.google.com/p/android-serialport-api) JNI代码编译的串口库类
以及利用 Linux 内核用户态的功能监听 GPIO 引脚

使用说明
---
```
implementation 'com.liux.android:io:x.y.z'
```

更新说明
---
### x.y.z_201x-xx-xx
    1.

### 0.2.2_2019-10-15
    1.修正串口重复关闭会崩溃的问题

### 0.2.1_2019-10-14
    1.改进 Gpio 原生层代码
    2.新增 Gpio 支持同时操作多个实例

### 0.2.0_2019-10-11
    1.构建出第一个版本