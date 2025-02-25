package com.cryo.core.component

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner


interface CyInterface {
    fun onAttached(lifecycleOwner: LifecycleOwner)
    fun onCreate(args: Bundle?)
    fun onResume()
    fun onPause()
    fun onDestroy()
    fun onDetached()
}