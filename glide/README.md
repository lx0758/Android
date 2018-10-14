Glide
===

使用说明
---
```
implementation 'com.liux.android:glide:x.y.z'
```

混淆参考
---
```
# Glide
## glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {   
  **[] $VALUES; 
  public *;
}
```

更新说明
---
### x.y.z_201x-xx-xx
    1.变更Maven依赖GroupID

### 0.2.6_2018-09-21
    1.回退采用发布版依赖版本
    2.优化视频自定义加载过程

### 0.2.5_2018-07-18
    1.更新 Glide 版本

### 0.2.4_2018-04-11
    1.支持新版 Http 库类

### 0.2.3_2018-02-27
    1.升级依赖版本

### 0.2.2_2018-01-16
    1.实现Glide自定义加载过程加载视频缩略图

### 0.2.1_2018-01-08
    1.升级依赖版本

### 0.2.0_2017-12-14
    1.升级SDK到27
    2.支持库到27+

### 0.1.1_2017-11-22
    1.升级依赖版本

### 0.1.0_2017-11-12
    1.完成从Framework分包