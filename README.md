# Cryo（Android插件化框架）

(android 动态框架)
[配置文件 hjson](http://hjson.org/)

####  1. 项目介绍
app 示例
core 核心包
annotation 注解包，Component注解在该包下
compiler APK自动生成，和annotation配合生成plugin.conf文件
plugin1 是一个示例插件包

#### 2. 如何集成？
 ```
 apply from: '../scripts/build_config.gradle'
 ```
 ```
 // 1.引入依赖
//    debugCompileOnly files("$rootDir/libs/core-debug.aar")
//    releaseCompileOnly files("$rootDir/libs/core-release.aar")
    implementation project(":core")

    // 2.kapt注解生成器
//    api files("$rootDir/libs/annotation.jar")
//    kapt files("$rootDir/libs/compiler.jar")
    implementation project(":annotation")
    kapt project(":compiler")

    // 3.添加依赖库
    implementation 'org.hjson:hjson:3.0.0'
    implementation 'com.github.bumptech.glide:glide:4.11.0'
 ```
#### 3. 插件包如何编译
buildZip 编译插件zip，会在模块下build/cryo目录
buildZipToAssets 编译后将文件发送到主模块/main/assets目录
buildZipToPluginDownloadFile 编译后将文件发送到插件下载目录（需要连接设备）

本项目模拟从assets下载，使用buildZipToAssets 编译

注意：插件包编译完成后成以下结构：
 ```
 build
   cryo
     dex
       classes.dex
     hash
       md5.txt
       sha256.txt
     libs
       main.jar
     target
       zip
         plugin1.zip
       plugin.conf
       source.dex
      build.sh
      plugin.conf
 ```
build/cryo/target/xxx.zip 为插件包，可用CyInstaller安装
build/cryo/hash/sha256.txt 是插件包的唯一证书，用于校验

本项目以最简单的方式提供一种在Android平台的插件化方案，集成方便后续扩展性强
除去gradle集成部分，本项目两个重点抽象
1. Component
2. View

Component作为载体，通过继承CyComponent，加注解@Component实现自定义
View作为显示但愿，通过继承CyView实现自定义
