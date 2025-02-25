package com.cryo.core.delegate

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

class CyLayout : CyView<CyLayout> {
    constructor(context: Context) : super(context)
    constructor(parent: CyView<*>): super(parent)
    override fun newView(): ViewGroup {
        return ConstraintLayout(context)
    }
}