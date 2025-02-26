package com.cryo.core.delegate

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.constraintlayout.widget.ConstraintLayout

class CyHScroller: CyView<CyHScroller> {
    constructor(context: Context) : super(context)
    constructor(parent: CyView<*>): super(parent)
    override fun newView(): View {
        return object: HorizontalScrollView(context) {
            override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
                post { super.addView(child, index, params) }
            }
        }.also {
            it.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}