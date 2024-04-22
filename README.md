# About
---
一个 `Android` 的代码库

# Use

## 子模块法

1. 进入项目执行:
   ```bash
   git submodule add -b master --name android git@github.com:lx0758/Android.git android
   ```

1. 然后在 `setting.gradle` 中按需添加模块.
   如果使用 `gradle` 则:
   ```gradle
   include ':libraries:service'
   include ':libraries:http'
   include ':libraries:io'
   ...
   
   def androidDir = rootProject.projectDir.path + File.separator + 'android'
   rootProject.children.each { project ->
       if (project.path.startsWith(':libraries')) {
           project.children.each { childProject ->
               childProject.projectDir = file(androidDir + File.separator + 'librarys' + File.separator + childProject.name)
           }
       }
   }
   ```
   如果使用 `kts` 则:
   ```dsl
   include(":libraries:service")
   include(":libraries:http")
   include(":libraries:io")
   ...
   
   val librariesDir = File(File(rootDir, "android"), "libraries")
   fun traversal(project: ProjectDescriptor) {
       project.children.forEach {
           if (it.path.startsWith(":libraries:")) {
               it.projectDir = File(librariesDir, it.name)
               return@forEach
           }
           traversal(it)
       }
   }
   traversal(rootProject)
   ```

1. 最后依赖使用.
   如果使用 `gradle` 则:
   ```gradle
   implementation project(':libraries:service')
   implementation project(':libraries:http')
   implementation project(':libraries:io')
   ```
   如果使用 `kts` 则:
   ```dsl
   implementation(project(":libraries:service"))
   implementation(project(":libraries:http"))
   implementation(project(":libraries:io"))
   ```

## 远程依赖

详见文档: [README.md](./libraries/README.md)

# License

[The MIT License Copyright (c) 2017-2024 6x](/LICENSE.txt)
