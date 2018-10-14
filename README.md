Library
===
库的用途在于逻辑层面的封装,在不侵入或少量侵入的前提下<br>
封装了一些和业务模式无强关联的库类<br>
并引用了一些时下较流行的三方开源库

[私有Maven镜像](http://maven.6xyun.cn)
---
```
repositories {
    ...
    maven {
        url 'http://maven.6xyun.cn/'
    }
    ...
}
```

License
---
[The MIT License Copyright (c) 2017 Liux](/LICENSE.txt)