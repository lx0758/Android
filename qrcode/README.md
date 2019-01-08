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
    1.修复一些可能引起崩溃的BUG

### 0.1.0_2018-10-14
    1.发布0.1.0版本