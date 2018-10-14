RX
===

使用说明
---
```
implementation 'com.liux.android:rx:x.y.z'
```

混淆参考
---
```
# RX
## rxjava2
-dontwarn io.reactivex.**
-keep class io.reactivex.** {*;}
## rxlifecycle2
-dontwarn com.trello.rxlifecycle2.**
-keep class com.trello.rxlifecycle2.** {*;}
```

更新说明
---
### x.y.z_201x-xx-xx
    1.变更Maven依赖GroupID

### 0.2.7_2018-09-21
    1.更新依赖库版本

### 0.2.6_2018-07-18
    1.更新依赖库版本

### 0.2.5_2018-04-05
    1.更新依赖库版本

### 0.2.4_2018-03-15
    1.增加通用事件转换

### 0.2.3_201x-02-28
    1.添加全局错误管理

### 0.2.2_2018-02-27
    1.升级依赖版本

### 0.2.1_2018-02-14
    1.优化代码

### 0.2.0_2018-02-13
    1.从http项目分离