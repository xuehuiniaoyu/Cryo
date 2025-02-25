package com.cryo

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.cryo.core.CyInstaller
import com.cryo.core.component.CyComponentLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TestActivity: AppCompatActivity() {
    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val drawer by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val pluginDownloader by lazy { PluginDownloader(this) }
    private val cyInstaller by lazy { CyInstaller(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pluginDownloader.download("plugin1.zip") { pluginZipFileName ->
            cyInstaller.install(pluginZipFileName)
            lifecycleScope.launch(Dispatchers.Main) {
                val view = CyComponentLoader.get(this@TestActivity).load("ui1").view
                container.addView(view)
                drawer.openDrawer(GravityCompat.START)
            }
        }
    }
}