package com.cryo.core.vb

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class VBind : ViewModel() {
    companion object {
        fun get(lifecycleOwner: LifecycleOwner): VBind {
            return ViewModelProvider(lifecycleOwner as ViewModelStoreOwner)[VBind::class.java]
        }
    }

    private val bindPool by lazy { hashMapOf<String, ArrayList<(Any?) -> Unit>>() }
    fun bind(key: String, onChanged: (Any?) -> Unit) {
        var list = bindPool[key]
        if(list == null) {
            list = arrayListOf()
        }
        list.add(onChanged)
        bindPool[key] = list
    }

    fun onBindValueChanged(key: String, value: Any?) {
        bindPool[key]?.forEach {
            it(value)
        }
    }

    override fun onCleared() {
        bindPool.clear()
    }
}