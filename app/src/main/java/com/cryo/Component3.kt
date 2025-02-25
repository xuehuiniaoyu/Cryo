package com.cryo

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import com.cryo.annotation.Component
import com.cryo.core.component.CyComponent
import com.cryo.core.delegate.CyLayout
import com.cryo.core.delegate.CyText

@Component("component3")
class Component3(view: ViewGroup) : CyComponent(view) {
    private var currentTab = "tab1"
    override fun onCreate(args: Bundle?) {
        super.onCreate(args)
        CyLayout(context).setWidth(100, "%").setHeight(100, "%").setBackgroundColor(
            Color.GREEN).build {
            when(currentTab) {
                "tab1" -> {
                    CyText(this).setCenterInParent().setText(currentTab.toString()).build()
                }
                "tab2" -> {
                    CyText(this).setCenterInParent().setText(currentTab.toString()).build()
                }
                "tab3" -> {
                    CyText(this).setCenterInParent().setText(currentTab.toString()).build()
                }
            }
        }.bind("onTabChanged") {
            this.clear()
            currentTab = it.toString()
            println("############$currentTab")
            reBuild()
        }.useView {
            setContentView(it)
        }
    }
}