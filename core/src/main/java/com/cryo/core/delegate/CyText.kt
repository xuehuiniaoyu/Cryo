package com.cryo.core.delegate

import android.util.TypedValue
import android.widget.TextView

class CyText(parent: CyView<*>): CyView<CyText>(parent) {
    private val mTextView by lazy { view as TextView }
    override fun newView(): TextView {
        return TextView(context)
    }

    fun setText(text: String): CyText {
        mTextView.text = text
        return this
    }

    fun setTextSize(size: Int, type: String? = null): CyText {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSize(size.toFloat(), type).size)
        return this
    }

    fun setTextSize(size: Float, type: String? = null): CyText {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSize(size, type).size)
        return this
    }

    fun setTextSize(size: Size): CyText {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSize(size).size)
        return this
    }

    fun setTextColor(color: Int): CyText {
        mTextView.setTextColor(color)
        return this
    }

    fun setGravity(gravity: Int): CyText {
        mTextView.gravity = gravity
        return this
    }
}