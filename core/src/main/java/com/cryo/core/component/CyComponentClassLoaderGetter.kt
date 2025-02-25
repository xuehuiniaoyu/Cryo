package com.cryo.core.component

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import dalvik.system.DexClassLoader
import java.io.File


class CyComponentClassLoaderGetter private constructor(private val context: Context) {
    companion object {
        const val LOG_TAG = "CyComponentClassLoaderGetter"
        @SuppressLint("StaticFieldLeak")
        private var instance: CyComponentClassLoaderGetter? = null
        fun init(context: Context) {
            instance = CyComponentClassLoaderGetter(context.applicationContext)
        }

        fun getInstance() = instance
    }

    private val pluginWorkspace by lazy { File(context.getExternalFilesDir(null), "plugins/workspace") }
    fun getClassLoader(pluginId: String): ClassLoader {
        if(pluginWorkspace.exists() && pluginWorkspace.isDirectory) {
            val pluginFile = pluginWorkspace.listFiles()?.find { it.name == pluginId }
            Log.d(LOG_TAG, "PluginFile:$pluginFile")
            if(pluginFile != null) {
                val libFile = File(pluginFile, "libs")
                Log.d(LOG_TAG, "libs:$libFile")
                return DexClassLoader(
                    File(pluginFile, "source.dex").absolutePath,
                    "optimized",
                    libFile.takeIf { it.exists() }?.absolutePath,
                    context.classLoader
                )
            }
        }
        return context.classLoader
    }
}