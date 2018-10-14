# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 忽略警告(威力太大,关闭)
#-ignorewarnings
# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclassmembers
# 不做预校验，preverify是proguard的四个步骤之一
# Android不需要preverify，去掉这一步能够加快混淆速度
-dontpreverify
# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# 避免混淆泛型
-keepattributes Signature
# 保留Annotation不混淆
-keepattributes *Annotation*
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 保持哪些类不被混淆：四大组件，应用类，配置类等等
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

# 保留Google原生服务需要的类
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# 保持在layout中写的onClick方法android:onclick="onClick"不被混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 保持 R 不被混淆
-keepclassmembers class **.R$* {
    public static <fields>;
}

# ==================================================================================================

# RX
## rxjava2
-dontwarn io.reactivex.**
-keep class io.reactivex.** {*;}
## rxlifecycle2
-dontwarn com.trello.rxlifecycle2.**
-keep class com.trello.rxlifecycle2.** {*;}

# LBS
## baidulbs
-dontwarn com.baidu.**
-dontwarn mapsdkvi.com.gdi.bgl.android.java.**
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.gdi.bgl.android.java.** {*;}
## amaplbs
-dontwarn com.amap.**
-dontwarn com.autonavi.**
-keep class com.amap.**{*;}
-keep class com.autonavi.**{*;}

# PAY
## alipay
-dontwarn com.alipay.**
-dontwarn com.ta.utdid2.**
-dontwarn com.ut.device.**
-dontwarn org.apache.http.**
-dontwarn org.json.alipay.**
-keep class com.alipay.** {*;}
-keep class com.ta.utdid2.** {*;}
-keep class com.ut.device.** {*;}
-keep class org.apache.http.** {*;}
-keep class org.json.alipay.** {*;}
## weixinpay
-dontwarn com.tencent.wxop.**
-dontwarn com.tencent.mm.**
-dontwarn com.tencent.a.**
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.** {*;}
-keep class com.tencent.a.** {*;}
## unionpay
-dontwarn com.unionpay.**
-dontwarn com.UCMobile.PayPlugin.**
-dontwarn org.simalliance.openmobileapi.**
-dontwarn cn.gov.pbc.tsm.client.mobile.android.bank.service.**
-keep class com.unionpay.** {*;}
-keep class com.UCMobile.PayPlugin.** {*;}
-keep class org.simalliance.openmobileapi.** {*;}
-keep class cn.gov.pbc.tsm.client.mobile.android.bank.service.** {*;}

# HTTP
## okhttp3
-dontwarn okhttp3.**
-keep class okhttp3.** {*;}
-dontwarn okio.**
-keep class okio.** {*;}
## retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** {*;}
-keepattributes Signature
-keepattributes Exceptions
## fastjson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** {*;}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod

# UTIL
# zxing
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** {*;}

# Glide
## glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
