# About
一个包含框架和库类的开源项目

# Use
## 子模块法
进入项目执行:
```bash
git submodule add -b master --name android git@github.com:lx0758/Android.git android
```
然后在 `setting.gradle` 中按需添加模块:
```gradle
include ':framework'

include ':librarys:abstracts'
include ':librarys:service'
include ':librarys:banner'
include ':librarys:downloader'
include ':librarys:http'
include ':librarys:list'
include ':librarys:pay'
include ':librarys:permission'
include ':librarys:mediaer'
include ':librarys:qrcode'
include ':librarys:io'
include ':librarys:tool'
include ':librarys:util'
include ':librarys:view'
include ':librarys:test'

def androidDir = rootProject.projectDir.path + File.separator + 'android'
rootProject.children.each { project ->
    if (project.path.startsWith(':framework')) {
        project.projectDir = file(androidDir + File.separator + project.name)
    }
    if (project.path.startsWith(':librarys')) {
        project.children.each { childProject ->
            childProject.projectDir = file(androidDir + File.separator + 'librarys' + File.separator + childProject.name)
        }
    }
}
```
然后依赖使用:
```gradle
implementation project(':framework')

implementation project(':librarys:list')
implementation project(':librarys:http')
implementation project(':librarys:tool')
```

## 本地引用
首先在 `local.properties` 中定义一个路径:
```properties
android.dir={path}
```
然后在 `setting.gradle` 中按需添加模块:
```gradle
include ':framework'

include ':librarys:abstracts'
include ':librarys:service'
include ':librarys:banner'
include ':librarys:downloader'
include ':librarys:http'
include ':librarys:list'
include ':librarys:pay'
include ':librarys:permission'
include ':librarys:mediaer'
include ':librarys:qrcode'
include ':librarys:io'
include ':librarys:tool'
include ':librarys:util'
include ':librarys:view'
include ':librarys:test'

Properties properties = new Properties()
InputStream inputStream = file('local.properties').newDataInputStream()
properties.load(inputStream)
def androidDir = properties.getProperty('android.dir')
inputStream.close()
rootProject.children.each { project ->
    if (project.path.startsWith(':framework')) {
        project.projectDir = file(androidDir + File.separator + project.name)
    }
    if (project.path.startsWith(':librarys')) {
        project.children.each { childProject ->
            childProject.parent = rootProject
            childProject.projectDir = file(androidDir + File.separator + 'librarys' + File.separator + childProject.name)
        }
    }
}
```
然后依赖使用:
```gradle
implementation project(':framework')

implementation project(':librarys:list')
implementation project(':librarys:http')
implementation project(':librarys:tool')
implementation project(':librarys:abstracts')
```

## 远程依赖
详见文档: [README.md](./librarys/README.md)

# License
[The MIT License Copyright (c) 2017-2024 6x](/LICENSE.txt)
