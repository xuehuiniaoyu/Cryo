# Cryo（Android插件化&组件化框架）

####  1. 项目介绍
* app 示例
* core 核心包
* annotation 注解包，Component注解在该包下
* compiler APK自动生成，和annotation配合生成plugin.conf文件
* plugin1 是一个示例插件包

#### 2. 如何集成？
1. 在setting.gradle中配置开源仓库
```
repositories {
	maven { url 'https://jitpack.io' }
}
```
2. 拷贝scripts文件夹到项目根目录
3. 在模块的build.gradle引入脚本
 ```
 apply from: '../scripts/build.gradle'
 ```
4. 修改build_config.gradle中applicationId配置为自己应用的包名

5. build.gradle示例：
```
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id 'kotlin-kapt'
}

apply from: '../scripts/build.gradle'

android {
    namespace 'com.cryo.cryodemo1'
    defaultConfig {
        applicationId "com.cryo.cryodemo1"
    }
}

dependencies {
    implementation libs.androidx.core.ktx
    implementation libs.androidx.appcompat
    implementation libs.material
    testImplementation libs.junit
    androidTestImplementation libs.androidx.junit
    androidTestImplementation libs.androidx.espresso.core
}
```
如果只想使用组件化功能不使用动态插件化，只需引入sdk即可
```
implementation 'com.github.xuehuiniaoyu:Cryo:latest.release'
kapt "com.github.xuehuiniaoyu:Cryo:latest.release"
implementation 'org.hjson:hjson:3.0.0'
implementation 'com.github.bumptech.glide:glide:4.11.0'
```
#### 3. 插件包如何编译
##### 右侧Gradle中找到对应模块/Tasks/Other/
* buildZip 编译插件zip，会在模块下build/cryo目录
* buildZipToAssets 编译后将文件发送到主模块/main/assets目录
* buildZipToPluginDownloadFile 编译后将文件发送到插件下载目录（需要连接设备）

本项目模拟从assets下载，使用buildZipToAssets编译

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
* build/cryo/target/zip/xxx.zip 为插件包，程序中可用CyInstaller安装
* build/cryo/hash/sha256.txt 是插件包的唯一证书，用于校验

本项目以最简单的方式提供一种在Android平台的插件化方案，集成方便后续扩展性强
除去gradle集成部分，本项目两个重点抽象
1. Component
2. View

* Component作为载体，通过继承CyComponent，加注解@Component实现自定义
* View作为显示但愿，通过继承CyView实现自定义

#### 4. 以上是集成和编译部分，以上做完就是集成到项目
1. 首先需要自定义Application进行初始化
```
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CyComponentLoader.init(this)
        // 注册校验规则,可从服务器下载
        // 插件包生成后，hash包里面会有sha256内容，将该值保存到服务器端和pluginId做映射，在请求接口时返回给客户端
        CyInstaller.pluginDexSha256Interface = object: PluginDexSha256Interface {
            override fun getPluginDexSha256(pluginId: String, callback: (String) -> Unit) {
                val sha256 = when(pluginId) {
                    "plugin1" -> "8ffe67ccc752d38eafe227c517686637a257d576cdb2eff85bcdfab6bb57317f"
                    else -> "#"
                }
                callback(sha256)
            }
        }
    }
}
```

CyInstaller.pluginDexSha256Interface接口是用于证书校验的，该接口返回可根服务器做交互，针对不同的插件对应不同的证书，如不需要可不实现该接口

2. 下面是一个Component的示例代码
```
@Component("demo_component")
class DemoComponent(view: ViewGroup) : CyComponent(view) {
    override fun onCreate(args: Bundle?) {
        super.onCreate(args)
        CyLayout(context).setWidth(0).setHeight(0).setCenterInParent().build {
            CyText(this).setText("hello world!").setGravity(Gravity.CENTER).build()
        }.useView(setContentView)
    }
}
```
3. 如何使用Component
```
class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = CyComponentLoader.get(this).load("demo_component").view
        setContentView(view)
    }
}
```
