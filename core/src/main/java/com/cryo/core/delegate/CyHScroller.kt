package com.cryo.core.delegate

import android.content.Context
import android.view.View
import android.widget.HorizontalScrollView

class CyHScroller: CyView<CyHScroller> {
    constructor(context: Context) : super(context)
    constructor(parent: CyView<*>): super(parent)
    override fun newView(): View {
        return HorizontalScrollView(context)
    }
}