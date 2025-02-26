package com.cryo.core.component

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.cryo.core.COMMON
import com.cryo.core.delegate.CyLayout
import com.cryo.core.delegate.CyText
import com.cryo.core.delegate.CyVScroller
import org.hjson.JsonObject
import org.hjson.JsonValue
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

class CyComponentLoader: ViewModel() {
    data class PluginConfig(val pluginId: String, val config: HashMap<String, JsonObject>)
    companion object {
        val config = hashMapOf<String, PluginConfig>()
        fun init(context: Context) {
            val configInputStream = context.assets.open("plugin.conf")
            loadPlugin(configInputStream)
            // 初始化Glide组件
            Glide.init(context, GlideBuilder().setLogLevel(android.util.Log.VERBOSE))
            CyComponentClassLoaderGetter.init(context)
            COMMON.set("#", File(context.getExternalFilesDir(null), "plugins/workspace").absolutePath)
        }

        fun remove(pluginId: String) {
            config.remove(pluginId)
        }

        fun initPlugin(conf: File) {
            loadPlugin(FileInputStream(conf))
        }

        private fun loadPlugin(inputStream: InputStream) {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val configRoot = JsonValue.readHjson(reader).asObject()
            val pluginId = configRoot.getString("pluginId", "app")
            val pluginConfig = config[pluginId] ?: PluginConfig(pluginId, hashMapOf())
            configRoot.get("mappings").asObject().forEach { member ->
                pluginConfig.config[member.name] = member.value.asObject()
            }
            config[pluginId] = pluginConfig
        }

        fun get(lifecycleOwner: LifecycleOwner): CyComponentLoader {
            return ViewModelProvider(lifecycleOwner as ViewModelStoreOwner)[CyComponentLoader::class.java].apply {
                if(widgetLoader == null) {
                    widgetLoader = CyLifecycle(lifecycleOwner)
                }
            }
        }
    }

    private var widgetLoader: CyLifecycle? = null
    val context: Context by lazy {
        widgetLoader?.lifecycleOwner?.let {
            when (it) {
                is AppCompatActivity -> it
                is Fragment -> it.context
                else -> null
            }
        } ?: error("Context is null!")
    }

    fun load(componentId: String, bundle: Bundle? = null) = load("app", componentId, bundle)
    fun load(pluginId: String, componentId: String, bundle: Bundle? = null): CyComponent {
        try {
            val pluginConfig = config[pluginId]?.config!!
            if (!pluginConfig.containsKey(componentId)) error("无效的插件id：$componentId")
            val configBean = pluginConfig[componentId]
            val clazz = (CyComponentClassLoaderGetter.getInstance()?.getClassLoader(pluginId)
                ?: javaClass.classLoader)?.loadClass(configBean?.get("class")?.asString()) ?: error(
                "Null!"
            )
            val instance = clazz.getConstructor(ViewGroup::class.java)
                .newInstance(ConstraintLayout(context)) as CyComponent
            widgetLoader?.init(instance, bundle)
            return instance
        } catch (ex: Exception) {
            val instance = object: CyComponent(ConstraintLayout(context)) {
                override fun onCreate(args: Bundle?) {
                    super.onCreate(args)
                    CyLayout(context).setFillParent().build {
                        val exception = Log.getStackTraceString(ex)
                        CyVScroller(this).build {
                            CyText(this).setText("插件：${pluginId}加载异常!\n$exception").setCenterInParent().setTextColor(
                                Color.RED).setTextSize(14, "dp").build()
                        }
                    }.useView(setContentView)
                }
            }
            widgetLoader?.init(instance, bundle)
            return instance
        }
    }
}