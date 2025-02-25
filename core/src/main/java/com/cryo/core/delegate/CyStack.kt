package com.cryo.core.delegate

import android.content.Context
import android.view.ViewGroup
import com.cryo.core.view.Stack

class CyStack : CyView<CyStack> {
    constructor(context: Context) : super(context)
    constructor(parent: CyView<*>): super(parent)
    override fun newView(): ViewGroup {
        return Stack(context)
    }

    fun setDirection(direction: Int): CyStack {
        (view as Stack).setDirection(direction)
        return this
    }
}