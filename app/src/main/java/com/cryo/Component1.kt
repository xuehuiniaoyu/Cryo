package com.cryo

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import com.cryo.annotation.Component
import com.cryo.core.COMMON
import com.cryo.core.delegate.CyLayout
import com.cryo.core.delegate.CyStack
import com.cryo.core.delegate.CyText
import com.cryo.core.component.CyComponent
import com.cryo.core.component.CyComponentLoader
import com.cryo.core.delegate.CyImage
import com.cryo.core.delegate.CyVScroller
import com.cryo.core.router.Router
import com.cryo.core.view.Stack
import com.google.android.material.snackbar.Snackbar
import java.io.File
import kotlin.io.path.Path

@Component("ui1")
class Component1(view: ViewGroup) : CyComponent(view) {
    private val router by lazy { Router(context) }
    override fun onCreate(args: Bundle?) {
        super.onCreate(args)
        CyLayout(context).setWidth(600).setHeight(600).setBackgroundColor(Color.GREEN).build {
            CyText(this).setId("aaa").setWidth(400).setText("hello").setBackgroundColor(Color.RED).setTop(null).setMargin(top = 20)
                .build().bind("clicked") {
                    setText(it.toString()).build()
                }.setOnClick {
                    onBindValueChanged("clicked", "hello${System.currentTimeMillis()}")
                }

            CyText(this).setId("bbb").setWidth(200).setText("world").setBackgroundColor(Color.YELLOW).setBelow("aaa").setPadding(20)
                .build().bind("clicked") {
                    setText(it.toString())
                }

            CyLayout(this).setBelow("bbb").setLeft("aaa").build {
                CyText(this).setText("1122").build().setOnClick {
                    Snackbar.make(view, "hello world!", Snackbar.LENGTH_LONG).show()
                }.setOnLongClick {
                    Snackbar.make(view, "2222222!", Snackbar.LENGTH_LONG).show()
                    true
                }
            }

            CyVScroller(this).build {
                CyStack(this).setMargin(top = 100).setDirection(Stack.DIRECTION_V).build {
                    for(i in 0 .. 50) {
                        CyText(this).setWidth(200).setText("world$i").setBackgroundColor(Color.BLUE)
                            .build()
                    }
                }
            }

            CyImage(this).setBackgroundColor(Color.GRAY).setWidth(100).setHeight(100)
                .setSrc(Path("/common/res/drawable/img.png")).build()
                //.setSrc("https://q2.itc.cn/q_70/images03/20250202/500118ad649b4a1cb2b79371013de177.jpeg").build()
                .setOnClick {
                    router.push("component2") {
                        it.putExtra("name", "jjjj${System.currentTimeMillis()}")
                    }
                }

            CyLayout(this).build {
                CyComponentLoader.get(lifecycleOwner).load("plugin1", "demo1").into(this.view)
            }.setWidth(100, "%").setHeight(100, "%").setBackgroundColor(Color.argb(100, 0, 0, 0))
        }.useView(setContentView)
    }
}