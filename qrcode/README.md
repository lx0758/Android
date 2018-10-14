QRCode
===

使用说明
---
```
implementation 'com.liux.android:qrcode:x.y.z'
```

混淆参考
---
```
# QRCode
# zxing
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** {*;}
```

更新说明
---
### x.y.z_201x-xx-xx
    1.更新支持库版本
    2.变更Maven依赖GroupID
    
### 0.1.0_2018-10-10
    1.发布第一个版本