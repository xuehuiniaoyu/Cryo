package com.cryo

import android.app.Application
import com.cryo.core.CyInstaller
import com.cryo.core.PluginDexSha256Interface
import com.cryo.core.component.CyComponentLoader

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