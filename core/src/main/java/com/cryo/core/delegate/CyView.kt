package com.cryo.core.delegate

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.cryo.core.util.LayoutUtils
import com.cryo.core.vb.VBind

abstract class CyView<out T : CyView<T>> {
    constructor(context: Context) {
        this.context = context
    }
    constructor(parent: CyView<*>) {
        this.context = parent.context
        this.parent = parent
    }
    private var parent: CyView<*>? = null
    val context: Context
    protected abstract fun newView(): View
    val view: View by lazy { newView() }
    private var space: (T.() -> Unit)? = null
    private val layoutParams: ConstraintLayout.LayoutParams by lazy {
        if(view.layoutParams == null) {
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        } else {
            view.layoutParams as ConstraintLayout.LayoutParams
        }
    }

    fun build(space: (T.() -> Unit)? = null): T {
        this.space = space
        space?.invoke(this as T)
        val container = parent?.view as? ViewGroup
        (view.parent as? ViewGroup)?.removeView(view)
        container?.addView(view)
        view.layoutParams = layoutParams
        return this as T
    }

    fun reBuild() {
        space?.invoke(this as T)
    }

    fun useView(action: (View) -> Unit) {
        action(view)
    }

    val add = fun(view: View) {
        (this@CyView.view as ViewGroup).addView(view)
    }

    fun clear() {
        (view as? ViewGroup)?.removeAllViews()
    }

    fun requestLayout(paramsChanged: Boolean = false): T {
        if(paramsChanged) {
            view.layoutParams = layoutParams
        }
        view.requestLayout()
        return this as T
    }

    fun setId(id: String): T {
        view.id = LayoutUtils.id(id)
        return this as T
    }

    data class Size(val size: Float, val type: String? = null)
    data class SizeResult(val size: Float, val error: String? = null)
    fun getSize(size: Float, type: String?): SizeResult {
        return when(type) {
            "dp" -> SizeResult(LayoutUtils.dpToPx(context, size))
            "%" -> SizeResult(size, "%")
            null, "px" -> SizeResult(size)
            else -> error("无效的类型：$type")
        }
    }

    fun getSize(size: Size?): SizeResult {
        if(size == null) return SizeResult(0F, null)
        return getSize(size.size, size.type)
    }

    fun setWidth(width: Int, type: String? = null): T = this.setWidth(width.toFloat(), type)
    fun setWidth(width: Float, type: String? = null): T {
        val result = getSize(width, type)
        if(result.error == null) {
            layoutParams.width = result.size.toInt()
        }
        else if(result.error == "%") {
            layoutParams.matchConstraintPercentWidth = 0.0F.coerceAtLeast(result.size)/100.0F
            layoutParams.matchConstraintDefaultWidth = 2
            layoutParams.width = 0
        }
        return this as T
    }
    fun setHeight(width: Int, type: String? = null): T = this.setHeight(width.toFloat(), type)
    fun setHeight(height: Float, type: String? = null): T {
        val result = getSize(height, type)
        if(result.error == null) {
            layoutParams.height = result.size.toInt()
        }
        else if(result.error == "%") {
            layoutParams.matchConstraintPercentHeight = 0.0F.coerceAtLeast(result.size)/100.0F
            layoutParams.matchConstraintDefaultHeight = 2
            layoutParams.height = 0
            println("matchConstraintPercentHeight=${layoutParams.matchConstraintPercentHeight}")
        }
        return this as T
    }
    fun setBackgroundColor(color: Int): T {
        view.setBackgroundColor(color)
        return this as T
    }
    fun setLeft(id: String?): T {
        layoutParams.startToEnd = if(id != null) LayoutUtils.id(id) else ConstraintLayout.LayoutParams.PARENT_ID
        return this as T
    }
    fun setRight(id: String?): T {
        layoutParams.endToStart = if(id != null) LayoutUtils.id(id) else ConstraintLayout.LayoutParams.PARENT_ID
        return this as T
    }
    fun setBelow(id: String?): T {
        layoutParams.topToBottom = if(id != null) LayoutUtils.id(id) else ConstraintLayout.LayoutParams.PARENT_ID
        return this as T
    }
    fun setAbove(id: String?): T {
        layoutParams.bottomToTop = if(id != null) LayoutUtils.id(id) else ConstraintLayout.LayoutParams.PARENT_ID
        return this as T
    }
    fun setStart(id: String?): T {
        layoutParams.startToStart = if(id != null) LayoutUtils.id(id) else ConstraintLayout.LayoutParams.PARENT_ID
        return this as T
    }
    fun setEnd(id: String?): T {
        layoutParams.endToEnd = if(id != null) LayoutUtils.id(id) else ConstraintLayout.LayoutParams.PARENT_ID
        return this as T
    }
    fun setTop(id: String?): T {
        layoutParams.topToTop = if(id != null) LayoutUtils.id(id) else ConstraintLayout.LayoutParams.PARENT_ID
        return this as T
    }
    fun setBottom(id: String?): T {
        layoutParams.bottomToBottom = if(id != null) LayoutUtils.id(id) else ConstraintLayout.LayoutParams.PARENT_ID
        return this as T
    }
    fun setMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) : T {
        layoutParams.setMargins(left, top, right, bottom)
        return this as T
    }
    fun setMargin(left: Size? = null, top: Size? = null, right: Size? = null, bottom: Size? = null) : T {
        layoutParams.setMargins(getSize(left).size.toInt(), getSize(top).size.toInt(), getSize(right).size.toInt(), getSize(bottom).size.toInt())
        return this as T
    }
    fun setMargin(size: Int = 0) : T {
        layoutParams.setMargins(size, size, size, size)
        return this as T
    }
    fun setMargin(size: Size? = null) : T {
        val sizeValue = getSize(size).size.toInt()
        layoutParams.setMargins(sizeValue, sizeValue, sizeValue, sizeValue)
        return this as T
    }
    fun setPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) : T {
        view.setPadding(left, top, right, bottom)
        return this as T
    }
    fun setPadding(left: Size? = null, top: Size? = null, right: Size? = null, bottom: Size? = null) : T {
        view.setPadding(getSize(left).size.toInt(), getSize(top).size.toInt(), getSize(right).size.toInt(), getSize(bottom).size.toInt())
        return this as T
    }
    fun setPadding(size: Int = 0) : T {
        view.setPadding(size, size, size, size)
        return this as T
    }
    fun setPadding(size: Size? = null) : T {
        val sizeValue = getSize(size).size.toInt()
        view.setPadding(sizeValue, sizeValue, sizeValue, sizeValue)
        return this as T
    }
    fun setHorizontalWidget(widget: Float): T {
        layoutParams.horizontalWeight = widget
        layoutParams.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD
        return this as T
    }
    fun setVerticalWidget(widget: Float): T {
        layoutParams.verticalWeight = widget
        layoutParams.verticalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD
        return this as T
    }
    fun setCenterInParent(): T {
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        return this as T
    }

    fun setFillParent(): T {
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        return this as T
    }

    fun setWidthFillParent(): T {
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        return this as T
    }

    fun setHeightFillParent(): T {
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        return this as T
    }
    fun setOnClick(listener: T.() -> Unit) : T {
        view.setOnClickListener {
            listener(this as T)
        }
        return this as T
    }
    fun setOnLongClick(listener: T.() -> Unit) : T {
        view.setOnLongClickListener {
            listener(this as T)
            true
        }
        return this as T
    }
    private val vBind by lazy { VBind.get(view.findViewTreeLifecycleOwner()!!) }
    fun bind(key: String, onChanged: T.(Any?) -> Unit): T {
        view.post {
            vBind.bind(key) { value ->
                onChanged(this as T, value)
            }
        }
        return this as T
    }
    fun onBindValueChanged(key: String, value: Any?) {
        view.post {
            vBind.onBindValueChanged(key, value)
        }
    }
}