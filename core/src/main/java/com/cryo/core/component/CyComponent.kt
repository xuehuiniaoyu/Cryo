package com.cryo.core.component

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner

open class CyComponent(val view: ViewGroup) : CyInterface {
    lateinit var lifecycleOwner: LifecycleOwner
    protected val bundle: Bundle by lazy { Bundle() }
    protected val context: Context by lazy {
        view.context ?: error("无法从lifecycleOwner中获取Context")
    }

    override fun onAttached(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
    }

    @CallSuper
    override fun onCreate(args: Bundle?) {
        if(args != null) {
            bundle.putAll(args)
        }
    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onDestroy() {

    }

    override fun onDetached() {

    }

    val setContentView = fun(view: View) {
        this.view.removeAllViews()
        this.view.addView(view)
    }

    fun into(view: View) {
        (view as ViewGroup).addView(
            this.view, ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}