package com.cryo.plugin1

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import com.cryo.annotation.Component
import com.cryo.core.component.CyComponent
import com.cryo.core.delegate.CyLayout
import com.cryo.core.delegate.CyText

@Component("demo1")
class DemoComponent(view: ViewGroup) : CyComponent(view) {
    override fun onCreate(args: Bundle?) {
        super.onCreate(args)
        CyLayout(context).build {
            CyText(this).setText("hello world! 2222").setBackgroundColor(Color.RED).build()
        }.useView(setContentView)
    }
}