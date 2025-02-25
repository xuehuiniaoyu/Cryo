package com.cryo.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cryo.core.component.CyComponentLoader

class CyComponentActivity: AppCompatActivity() {
    private val componentId by lazy { intent.getStringExtra("componentId") ?: error("没有指定组件ID") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = CyComponentLoader.get(this).load(componentId, intent.extras).view
        setContentView(view)
    }
}