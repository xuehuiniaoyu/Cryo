package com.cryo.core

import android.content.Context
import com.cryo.core.component.CyComponentLoader
import com.cryo.core.util.DebugLogger
import com.cryo.core.util.FileUtil
import org.hjson.JsonValue
import org.hjson.Stringify
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.security.MessageDigest

/**
 * 插件安装管理器
 * 插件的安装过程：
 * 1. 从服务器下载插件包到应用本地 files/download 目录（也可以自定义）
 * 2. 下载后调用 install 函数进行安装，install的调用方式有两种：
 *    1.从 files/download 安装，该方法只需传入文件名即刻
 *    2.从自定义目录安装，该方法需要传入完整的文件路径
 * 3. sha256校验，CyInstaller提供了sha256返回的接口 pluginDexSha256Interface: PluginDexSha256Interface
 *    在使用插件之前实现该接口
 * 4. 校验通过后会自动初始化插件，可直接使用CyComponentLoader.get(lifecycleOwner).load()来获取插件Component
 */
class CyInstaller(context: Context) {
    companion object {
        const val TAG_INSTALLED = "installed"
        fun getDownloadDirectory(context: Context): File {
            val downloadDir = File(context.applicationContext.getExternalFilesDir(null), "download")
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            return downloadDir
        }

        var pluginDexSha256Interface: PluginDexSha256Interface? = null

//    @Throws(Exception::class)
//    fun calculateMD5(file: File?): String {
//        val digest = MessageDigest.getInstance("MD5")
//        FileInputStream(file).use { fis ->
//            val buffer = ByteArray(1024)
//            var length: Int
//            while (fis.read(buffer).also { length = it } != -1) {
//                digest.update(buffer, 0, length)
//            }
//        }
//        val hashBytes = digest.digest()
//        val hexString = StringBuilder()
//        for (b in hashBytes) {
//            hexString.append(String.format("%02x", b))
//        }
//        return hexString.toString()
//    }

        @Throws(java.lang.Exception::class)
        fun calculateSHA256(file: File?): String {
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(1024)
                var length: Int
                while (fis.read(buffer).also { length = it } != -1) {
                    digest.update(buffer, 0, length)
                }
            }
            val hashBytes = digest.digest()
            val hexString = java.lang.StringBuilder()
            for (b in hashBytes) {
                hexString.append(String.format("%02x", b))
            }
            return hexString.toString()
        }



        private fun getPluginDexSha256(pluginId: String, callback: (String?) -> Unit) {
            pluginDexSha256Interface?.getPluginDexSha256(pluginId, callback)
                ?: callback(null)
        }

        private fun checkSha256(pluginFile: File, sha256: String?) {
            if (sha256 == null) return
            val fileSha256 = calculateSHA256(pluginFile)
            if (fileSha256 != sha256) error("签名错误！")
        }
    }

    private val logger by lazy { DebugLogger("CyInstaller") }
    private val pluginWorkspace by lazy {
        val workspace = File(context.getExternalFilesDir(null), "plugins/workspace")
        if (!workspace.exists()) {
            workspace.mkdirs()
        }
        workspace
    }
    private val downloadDirectory by lazy { getDownloadDirectory(context) }
    private val tmpDir by lazy { File(pluginWorkspace, "tmp") }
    private val sp by lazy { context.getSharedPreferences("plugin", Context.MODE_PRIVATE) }


    /**
     * 从下载目录安装插件
     * @see downloadDirectory
     */
    fun install(pluginZipFileName: String) {
        this.install(File(downloadDirectory, pluginZipFileName))
    }

    /**
     * 安装插件
     */
    @Synchronized
    fun install(pluginZipFile: File) {
        if (!pluginZipFile.exists()) {
            logger.debug("插件包不存在！")
            return
        }
        logger.debug("开始安装插件：$pluginZipFile")
        // 解压插件
        val unZipFileName = pluginZipFile.name.substringBefore(".")
        val unZipFile = File(tmpDir, unZipFileName)
        FileUtil.unzipFile(pluginZipFile, unZipFile)
        logger.debug("解压完成，准备读取pluginId")
        // 读取配置文件获取pluginId
        val configFile = File(tmpDir, "${unZipFileName}/plugin.conf")
        val reader = BufferedReader(InputStreamReader(FileInputStream(configFile)))
        val configJsonObject = JsonValue.readHjson(reader).asObject()
        val pluginId = configJsonObject.getString("pluginId", "app")
        logger.debug("读取到pluginId为：${pluginId}, 准备拷贝文件")
        val tmpFile = File(tmpDir, unZipFileName)
        FileUtil.copy(tmpFile, File(pluginWorkspace, pluginId))
        // 保存本地安装记录
        logger.debug("拷贝文件完成")
        FileUtil.deleteFile(tmpFile)
        logger.debug("删除临时文件，记录安装")
        val localPluginInstalledRecord =
            JsonValue.readHjson(sp.getString(TAG_INSTALLED, "[]")).asArray()
        if (localPluginInstalledRecord.find { it.asString() == pluginId } == null) {
            localPluginInstalledRecord.add(pluginId)
        }
        sp.edit().putString(TAG_INSTALLED, localPluginInstalledRecord.toString(Stringify.HJSON))
            .apply()
        logger.debug("安装完成！")
        loadPlugins(pluginId)
    }

    /**
     * 卸载插件
     */
    @Synchronized
    fun uninstall(pluginId: String) {
        val localPluginInstalledRecord =
            JsonValue.readHjson(sp.getString(TAG_INSTALLED, "[]")).asArray()
        localPluginInstalledRecord.removeAll { it.asString() == pluginId }
        sp.edit().putString(TAG_INSTALLED, localPluginInstalledRecord.toString(Stringify.HJSON))
            .apply()
        CyComponentLoader.remove(pluginId)
        logger.debug("卸载完成！")
    }

    /**
     * 获取所有安装插件
     */
    fun getPlugins(): List<String> {
        return JsonValue.readHjson(sp.getString(TAG_INSTALLED, "[]")).asArray().map {
            it.asString()
        }
    }

    fun loadPlugins(vararg pluginIds: String) {
        (pluginIds.takeIf { it.isNotEmpty() }?.asList() ?: getPlugins()).forEach { pluginId ->
            getPluginDexSha256(pluginId) { sha256 ->
                // dex证书校验
                val dexFile = File(pluginWorkspace, "${pluginId}/source.dex")
                if (dexFile.exists()) {
                    checkSha256(dexFile, sha256)
                    val configFile = File(pluginWorkspace, "${pluginId}/plugin.conf")
                    CyComponentLoader.initPlugin(configFile)
                }
            }
        }
    }
}

interface PluginDexSha256Interface {
    fun getPluginDexSha256(pluginId: String, callback: (String) -> Unit)
}