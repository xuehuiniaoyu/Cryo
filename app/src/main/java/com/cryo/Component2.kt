package com.cryo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.cryo.annotation.Component
import com.cryo.core.component.CyComponent
import com.cryo.core.component.CyComponentLoader
import com.cryo.core.delegate.CyLayout
import com.cryo.core.delegate.CyStack
import com.cryo.core.delegate.CyText
import com.cryo.core.view.Stack

@Component("component2")
class Component2(view: ViewGroup) : CyComponent(view) {
    override fun onCreate(args: Bundle?) {
        super.onCreate(args)
        CyLayout(context).setWidth(100, "%").setHeight(50, "%").setBackgroundColor(Color.RED).build {
            // 底部栏
            CyStack(this).setWidth(100, "%").setId("bottomTab").setDirection(Stack.DIRECTION_H).setHeight(45, "dp").setBackgroundColor(
                Color.GRAY).setBottom(null).build {
                CyText(this).setWidth(33.3F, "%").setHeight(100, "%").setGravity(Gravity.CENTER).setText("tab1").build().setOnClick { onBindValueChanged("onTabChanged", "tab1") }
                CyText(this).setWidth(33.3F, "%").setHeight(100, "%").setGravity(Gravity.CENTER).setText("tab2").build().setOnClick { onBindValueChanged("onTabChanged", "tab2") }
                CyText(this).setWidth(33.3F, "%").setHeight(100, "%").setGravity(Gravity.CENTER).setText("tab3").build().setOnClick { onBindValueChanged("onTabChanged", "tab3") }
            }
            // 内容
            CyLayout(this).setAbove("bottomTab").setTop(null).setWidth(100, "%").setHeight(0).build {
                CyComponentLoader.get(lifecycleOwner).load("component3", args).into(this.view)
            }
        }.useView {
            setContentView(it)
        }
    }
}