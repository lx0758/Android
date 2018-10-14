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
    1.

### 0.1.0_2018-10-14
    1.发布0.1.0版本