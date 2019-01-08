Pay
===

使用说明
---
```
implementation 'com.liux.android:pay:x.y.z'
```

混淆参考
---
```
# PAY
## alipay
-dontwarn com.alipay.**
-keep class com.alipay.** {*;}
## unionpay
-dontwarn com.unionpay.**
-keep class com.unionpay.** {*;}
```

更新说明
---
### x.y.z_201x-xx-xx
    1.

### 0.1.3_2019-01-08
    1.升级支付宝SDK

### 0.1.2_2018-10-31
    1.调整微信回调代码
    2.更改工具类名称

### 0.1.0_2018-10-14
    1.发布0.1.0版本