package com.cryo.core.util

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class LazyCall(scope: LifecycleCoroutineScope? = null) {
    private var lifecycleScope: LifecycleCoroutineScope? = scope
    private var called = false
    private val lazyQueue = LinkedList<(Any?) -> Unit>()
    private var mData: Any? = null

    fun onLazy(context: CoroutineContext = EmptyCoroutineContext, block: () -> Unit) {
        this.onLazyParametric(context) {
            block()
        }
    }

    fun onLazyParametric(context: CoroutineContext = EmptyCoroutineContext, block: (Any?) -> Unit) {
        val lazyEx = fun() {
            if(!called) {
                lazyQueue.addLast(block)
            }
            else {
                block(mData)
            }
        }
        lifecycleScope?.launch(context) {
            lazyEx()
        } ?: lazyEx()
    }

    fun call(obj: Any? = null) {
        this.mData = obj
        val callEx = fun() {
            if(!called) {
                called = true
                while (lazyQueue.peek() != null) {
                    lazyQueue.poll()?.invoke(obj)
                }
            }
        }
        lifecycleScope?.launch {
            callEx()
        } ?: callEx()
    }

    fun clear(): LazyCall {
        lazyQueue.clear()
        mData = null
        return this
    }
    fun reset(lifecycleScope: LifecycleCoroutineScope? = null) {
        this.lifecycleScope = lifecycleScope
        called = false
    }
}