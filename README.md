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

include ':libraries:abstracts'
include ':libraries:service'
include ':libraries:banner'
include ':libraries:downloader'
include ':libraries:http'
include ':libraries:list'
include ':libraries:pay'
include ':libraries:permission'
include ':libraries:mediaer'
include ':libraries:qrcode'
include ':libraries:io'
include ':libraries:tool'
include ':libraries:util'
include ':libraries:view'
include ':libraries:test'

def androidDir = rootProject.projectDir.path + File.separator + 'android'
rootProject.children.each { project ->
    if (project.path.startsWith(':framework')) {
        project.projectDir = file(androidDir + File.separator + project.name)
    }
    if (project.path.startsWith(':libraries')) {
        project.children.each { childProject ->
            childProject.projectDir = file(androidDir + File.separator + 'librarys' + File.separator + childProject.name)
        }
    }
}
```
然后依赖使用:
```gradle
implementation project(':framework')

implementation project(':libraries:list')
implementation project(':libraries:http')
implementation project(':libraries:tool')
```

## 本地引用
首先在 `local.properties` 中定义一个路径:
```properties
android.dir={path}
```
然后在 `setting.gradle` 中按需添加模块:
```gradle
include ':framework'

include ':libraries:abstracts'
include ':libraries:service'
include ':libraries:banner'
include ':libraries:downloader'
include ':libraries:http'
include ':libraries:list'
include ':libraries:pay'
include ':libraries:permission'
include ':libraries:mediaer'
include ':libraries:qrcode'
include ':libraries:io'
include ':libraries:tool'
include ':libraries:util'
include ':libraries:view'
include ':libraries:test'

Properties properties = new Properties()
InputStream inputStream = file('local.properties').newDataInputStream()
properties.load(inputStream)
def androidDir = properties.getProperty('android.dir')
inputStream.close()
rootProject.children.each { project ->
    if (project.path.startsWith(':framework')) {
        project.projectDir = file(androidDir + File.separator + project.name)
    }
    if (project.path.startsWith(':libraries')) {
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

implementation project(':libraries:list')
implementation project(':libraries:http')
implementation project(':libraries:tool')
implementation project(':libraries:abstracts')
```

## 远程依赖
详见文档: [README.md](./librarys/README.md)

# License
[The MIT License Copyright (c) 2017-2024 6x](/LICENSE.txt)
