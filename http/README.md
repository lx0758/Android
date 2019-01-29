Http
===
基于 OkHttp3 封装的Http客户端
0. 全局单例模式
1. GET/HEAD/POST/DELETE/PUT/PATCH六种方法的同步/异步访问调用
2. Retorfit2 + RxJava2 支持
3. 数据解析使用 FastJson
4. 请求头/请求参数回调
5. 超时时间/BaseUrl/UserAgent灵活设置
6. 请求数据进度回调支持
7. 流传输能力

使用说明
---
```
implementation 'com.liux.android:http:x.y.z'
```

混淆参考
---
```
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
```

注意事项
---
    1.使用Retorfit以根Url方式创建请求时,如果全局 Base 的 根Url 以其重叠将无法无法正确匹配
      过程:
      初始 BaseUrl 为 http://api.baidu.com/api/
      全局 BaseUrl 为 http://api.google.com/v1.0/
      请求接口设置为 @GET("/api/xxx")
      理想状态应该为 http://api.google.com/api/xxx
      实际状态变成为 http://api.google.com/v1.0/xxx
      
      原因:
      初始化请求以"/"开头,经过Retorfit解析后变为
      http://api.baidu.com/api/xxx
      在经过 BaseUrl 拦截器替换其中 http://api.baidu.com/api/ 为 http://app.google.com/v1.0/
      最终变成 http://api.google.com/v1.0/xxx
      
      结论:
      即当初始 BaseUrl 的根Path和请求根Path发生重叠时出现此现象
      
      注:
      假设BaseUrl为 http://api.6xyun.cn/v1.0/
      根Url => @GET("/api/query")
      根Url方式结果 http://api.6xyun.cn/api/query
      非根Url => @GET("api/query")
      非根Url方式结果 http://api.6xyun.cn/v1.0/api/query
    
    2.使用 MultipartBody 上传 InputStream 时,应避免多个线程共享 InputStream
      因为上传过程中 InputStream 会被 读取/重置 多次,在多线程环境下会造成数据紊乱
      故,在使用时应达到每个线程单独使用一个 InputStream 实例的条件
      
      另:在发送 InputStream 输入源时, 如果 InputStream.available() 方法不能一次返回流完整长度,则应返回 -1
    
    3.在使用 MultipartBody 传输 byte[] 或 InputStream 时,因为框架发送的是原始二进制数据,
      所以服务端接收也必须接收原始数据.否则有可能造成数据被强行"字符"转换,导致获得的数据损坏
      并且,传输 InputStream 到服务器也是按照 byte[] 方式接收
      
      /**
       * 错误用例(Java):
       * @param modelMap
       * @param id
       * @param name
       * @param data
       * @param stream
       * @param file
       * @return
       */
      @PostMapping(value = "/api")
      public String upload(ModelMap modelMap, int[] id, String[] name, byte[] data, byte[] stream, MultipartFile file) {
          System.out.println("data:" + Arrays.toString(data));
          System.out.println("stream:" + Arrays.toString(stream));
          return "Done!";
      }
      
      /**
       * 正确用例(Java):
       * @param modelMap
       * @param request
       * @param id
       * @param name
       * @param file
       * @return
       */
      @PostMapping(value = "/api")
      public String upload(ModelMap modelMap, HttpServletRequest request, int[] id, String[] name, MultipartFile file) {
          try {
              byte[] data = readInputStream(request.getPart("data").getInputStream());
              byte[] stream = readInputStream(request.getPart("stream").getInputStream());
              System.out.println("data:" + Arrays.toString(data));
              System.out.println("stream:" + Arrays.toString(stream));
          } catch (Exception e) {
              
          }
          return "Done!";
      }
      
      详情参见:
      https://github.com/lx0758/Java-Demo/tree/master/api
      
    3.参数拦截器处理 MultipartBody 时,会将 text 类型的文本参数逆解析
      此动作可能会导致 Part 某些自定义的 Header 丢失,详情请看源码

更新说明
---
### x.y.z_201x-xx-xx
    1.修复表单内容被二次编码的BUG
    2.升级依赖库库引用版本
    3.修改缓存目录位置

### 0.1.5_2019-01-23
    1.升级FastJson为Java版本, 旧版本有反序列化坑(会缓存解析器,但没有成功在新项目复现)
    2.降级 OkHttp 至 3.11.0, 原 3.12.x 版本上传文件构建 Part 不支持中文文件名
      详情: https://github.com/square/okhttp/issues/4564

### 0.1.4_2019-01-08
    1.升级支持库引用版本

### 0.1.3_2018-11-16
    1.优化完善代码
    2.修正发版插件生成错误的pom.xml文件

### 0.1.0_2018-10-14
    1.发布0.1.0版本