package com.cryo.core.router

import android.content.Context
import android.content.Intent
import com.cryo.core.CyComponentActivity

class Router(private val context: Context) {
    fun push(componentId: String, also: (Intent) -> Unit = {}) {
        val intent = Intent(context, CyComponentActivity::class.java)
        intent.putExtra("componentId", componentId)
        intent.also(also)
        context.startActivity(intent)
    }
}