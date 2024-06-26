Http
===
基于 OkHttp3 封装的Http客户端
1.GET/HEAD/POST/DELETE/PUT/PATCH六种方法的同步/异步访问调用
2.请求头/请求参数回调
3.超时时间/UserAgent灵活设置
4.请求数据进度回调支持
5.流传输能力

使用说明
---
```
implementation 'cn.6xyun.android:http:x.y.z'
```

注意事项
---
    1.使用 MultipartBody 上传 InputStream 时,应避免多个线程共享 InputStream
      因为上传过程中 InputStream 会被 读取/重置 多次,在多线程环境下会造成数据紊乱
      故,在使用时应达到每个线程单独使用一个 InputStream 实例的条件
      
      另:在发送 InputStream 输入源时, 如果 InputStream.available() 方法不能一次返回流完整长度,则应返回 -1
    
    2.在使用 MultipartBody 传输 byte[] 或 InputStream 时,因为框架发送的是原始二进制数据,
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
### x.y.z_202x-xx-xx
    1. 

### 0.2.23_2024-04-25
    1. 移除 Retorfit 组件 & 取消单例用法

### 0.2.22_2021-03-15
    1. 新增 HttpDNS 容错支持
    2. 替换 Json 处理框架 FastJson 为 Jackson
    3. 移除支持 RxJava 和 RxJava2 适配器的代码

### 0.2.18_2020-12-30
    1. 新增 Cookie 内存存储支持

### 0.2.16_2020-12-07
    1. 新增证书加载工具

### 0.2.14_2020-09-07
    1. 移除 Cookie 处理器

### 0.2.12_2020-06-02
    1. 升级 OkHttp/Retorfit 依赖版本
    2. 修复不能上传流的 BUG

### 0.2.11_2020-05-09
    1. 兼容 Q 支持上传 Uri 方式
    2. 升级 OkHttp/Retorfit 依赖版本

### 0.2.10_2020-04-15
    1. 修复手动请求异常中断问题

### 0.2.9_2020-03-30
    1. 优化日志打印拦截器

### 0.2.8_2020-03-29
    1. 改变文件下载实现

### 0.2.6_2020-03-29
    1. 优化日志打印拦截器逻辑
    2. 优化进度监听回调逻辑
    3. 移除 Http 缓存配置
    4. 新增快捷下载功能

### 0.2.5_2020-03-27
    1. 修正 Request 无法被取消的问题

### 0.2.4_2020-03-17
    1. 调整 WebSocket 心跳

### 0.2.3_2020-01-13
    1. 紧急升级 OKHttp 版本, 旧版本有 BUG
    
### 0.2.2_2020-01-07
    1. 优化 HttpDNS 配置
    2. 优化请求创建器逻辑
    3. 优化请求拦截器逻辑

### 0.2.1_2019-12-18
    1. DNS 新增重试支持
    2. 升级支持库

### 0.2.0_2019-08-26
    1. 支持库迁移至AndroidX

### 0.1.12_2019-08-14
    1. 更新 FastJSON 为安卓专用版

### 0.1.11_2019-08-13
    1. 更新OkHttp & Retorfit版本
    2. 修复释放 Http 后不能及时回收资源

### 0.1.10_2019-06-29
    1. 添加DNS超时及HttpDns实现

### 0.1.9_2019-06-28
    1. 统一设置超时时间的单位

### 0.1.8_2019-06-28
    1. 优化设置超时时间的单位

### 0.1.7_2019-04-24
    1. 增加逆初始化支持

### 0.1.6_2019-04-17
    1. 修复表单内容被二次编码的BUG
    2. 升级依赖库库引用版本
    3. 修改缓存目录位置

### 0.1.5_2019-01-23
    1. 升级FastJson为Java版本, 旧版本有反序列化坑(会缓存解析器,但没有成功在新项目复现)
    2. 降级 OkHttp 至 3.11.0, 原 3.12.x 版本上传文件构建 Part 不支持中文文件名
      详情: https://github.com/square/okhttp/issues/4564

### 0.1.4_2019-01-08
    1. 升级支持库引用版本

### 0.1.3_2018-11-16
    1. 优化完善代码
    2. 修正发版插件生成错误的pom.xml文件

### 0.1.0_2018-10-14
    1. 发布0.1.0版本