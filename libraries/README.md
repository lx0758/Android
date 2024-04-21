# Library
库的用途在于逻辑层面的封装,在不侵入或少量侵入的前提下
封装了一些和业务模式无强关联的库类
并引用了一些时下较流行的三方开源库

# [abstracts](./abstracts/README.md)
AbstractsActivity重定义了生命周期细节,增加输入法与触控事件的逻辑,实现可自定义的沉浸式状态栏
AbstractsFragment实现了Fragment懒加载模型,处理某些情况下Fragment状态异常的问题
AbstractsDialog扩展沉浸式的Dialog

# [service](./service/README.md)
提供一个便捷的服务治理框架

# [banner](./banner/README.md)
实现一个适配器模式的伪无限滚动的Banner

# [downloader](./downloader/README.md)
全局/单线程/多任务/断点续传HTTP下载器

# [http](./http/README.md)
封装OkHttp3和Retrofit2的全局单例Http客户端,并添加了持久化Cookie相关能力

# [io](./io/README.md)
基于 Android中Linux内核 的 串口/GPIO/I2C 通信库类的封装

# [list](./list/README.md)
基于RecyclerView的Adapter,ItemDecoration和ViewHolder的扩展封装
封装选择控制,数据源类型加载,Header/Footer能力的Adapter
封装类似ListView中分割线控制的ColorDecoration
封装灵活的Holder,内部缓存控件提升性能

# [mediaer](./mediaer/README.md)
媒体文件选取/拍摄/录制/加载的库类

# [pay](./pay/README.md)
基于支付宝/微信支付/银联支付的支付逻辑封装

# [permission](./permission/README.md)
封装链式调用的运行时/悬浮窗/安装权限申请工具类

# [qrcode](./qrcode/README.md)
基于[zxing](https://github.com/zxing/zxing)的一个二维码扫描/识别/生成库

# [tool](./tool/README.md)/[util](/util/README.md)/[view](/view/README.md)
其他一些库类/组件/控件的集合

# [test](./test/README.md)
搭建基于 JUnit4 Mockito PowerMockito Robolectric 的一个单元测试环境