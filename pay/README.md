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
    1.变更Maven依赖GroupID

### 0.2.4_2018-09-21
    1.升级支付宝&微信的SDK版本

### 0.2.3_2018-02-27
    1.升级依赖版本

### 0.2.2_2018-02-13
    1.整合银联支付实现

### 0.2.1_2017-12-15
    1.修复SDK权限BUG

### 0.2.0_2017-12-14
    1.升级SDK到27
    2.支持库到27+

### 0.1.0_2017-11-12
    1.完成从Framework分包