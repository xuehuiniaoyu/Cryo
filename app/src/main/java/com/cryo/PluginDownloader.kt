package com.cryo

import android.content.Context
import com.cryo.core.CyInstaller
import com.cryo.core.util.FileUtil
import java.io.File
import kotlin.concurrent.thread

/**
 * 插件下载管理
 * 示例模拟从assets（服务端）将插件文件下载到本地安装
 */
class PluginDownloader(context: Context) {
    private val server by lazy { context.applicationContext.assets }
    private val downloadDirectory by lazy { CyInstaller.getDownloadDirectory(context) }
    fun download(pluginZipFileName: String, success: (String) -> Unit) {
        val fileInputStream = server.open(pluginZipFileName)
        thread {
            FileUtil.copyFileTo(fileInputStream, File(downloadDirectory, pluginZipFileName))
            success(pluginZipFileName)
        }
    }
}