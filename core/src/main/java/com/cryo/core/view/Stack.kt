package com.cryo.core.view

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.cryo.core.util.DebugLogger
import java.util.UUID

class Stack(context: Context) : ConstraintLayout(context) {
    companion object {
        const val DIRECTION_H = 0
        const val DIRECTION_V = 1
        private val logger = DebugLogger("Stack")
    }

    private var mDirection = DIRECTION_V // 默认垂直
    private var lastChild: View? = null
    private val constraintSet = ConstraintSet()

    fun setDirection(direction: Int): Stack {
        this.mDirection = direction
        return this
    }

    private var ready = false
    override fun onViewAdded(view: View?) {
        super.onViewAdded(view)
        if(view?.id == -1) {
            view.id = createViewId()
        }
        val lastId = lastChild?.id ?: -1
        val afterLoad = {
            logger.debug("after onLoad.. $lastId $view")
            constraintSet.clone(this)
            align(view, lastId, !ready)
            if(!ready) {
                ready = true
            }
            constraintSet.applyTo(this)
        }
        view?.post {
            afterLoad()
        }
        lastChild = view
    }

    private fun align(view: View?, dstId: Int, isFirstChild: Boolean) {
        if (isFirstChild) {
            init(view)
        } else {
            append(view, dstId)
        }
    }

    private fun init(view: View?) {
        when (mDirection) {
            DIRECTION_H -> {
                constraintSet.connect(
                    view!!.id,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
                )
            }
            DIRECTION_V -> {
                constraintSet.connect(
                    view!!.id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
            }
        }
    }

    private fun append(view: View?, dstId: Int) {
        println("stack append:${view?.id}")
        when (mDirection) {
            DIRECTION_H -> {
                constraintSet.connect(
                    view!!.id,
                    ConstraintSet.START,
                    dstId,
                    ConstraintSet.END
                )
            }
            DIRECTION_V -> {
                constraintSet.connect(
                    view!!.id,
                    ConstraintSet.TOP,
                    dstId,
                    ConstraintSet.BOTTOM
                )
            }
        }
    }

    private fun createViewId(): Int {
        return UUID.randomUUID().hashCode()
    }
}