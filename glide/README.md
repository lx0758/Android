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
    1.

### 0.1.0_2018-10-14
    1.发布0.1.0版本