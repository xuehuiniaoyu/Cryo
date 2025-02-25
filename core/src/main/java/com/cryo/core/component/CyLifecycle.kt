package com.cryo.core.component

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class CyLifecycle(val lifecycleOwner: LifecycleOwner?) {
    init {
        lifecycleOwner?.lifecycle?.addObserver(object: LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                val observer = this
                source.lifecycleScope.launch(Dispatchers.Main) {
                    when(event) {
                        Lifecycle.Event.ON_RESUME -> widgetPool.iterator().forEach { ui -> ui.onResume() }
                        Lifecycle.Event.ON_PAUSE -> widgetPool.iterator().forEach { ui -> ui.onPause() }
                        Lifecycle.Event.ON_DESTROY -> {
                            source.lifecycle.removeObserver(observer)
                            widgetPool.iterator().forEach { ui ->
                                ui.onDestroy();
                                ui.onDetached()
                            }
                            widgetPool.clear()
                        }
                        else -> {}
                    }
                }
            }
        })
    }

    private val widgetPool by lazy { CopyOnWriteArrayList<CyInterface>() }

    fun init(widget: CyInterface, bundle: Bundle? = null) {
        if(!widgetPool.contains(widget)) {
            widgetPool.add(widget)
            lifecycleOwner?.lifecycleScope?.launch(Dispatchers.Main) {
                widget.onAttached(lifecycleOwner)
                widget.onCreate(bundle)
            }
        }
    }
}