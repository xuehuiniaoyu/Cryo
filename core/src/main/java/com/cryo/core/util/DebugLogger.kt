package com.cryo.core.util

import android.content.Context
import android.util.Log
import java.io.File
import java.util.concurrent.Executors

class DebugLogger(private val space: String = "global", logsDir: File? = staticLogsDir) {
    companion object {
        private const val LOG_TAG = "DebugHelper."
        var staticLogsDir: File? = null
        fun defaultLogsDir(context: Context) = File(context.getExternalFilesDir(null), "logs/debugger")

        private val executor by lazy { Executors.newSingleThreadExecutor() }
    }

    private val spaceTag get() = "$LOG_TAG($space)"
    fun error(ex: Exception) {
        executor.submit {
            Log.e(spaceTag, android.util.Log.getStackTraceString(ex))
        }
    }

    fun error(any: Any?) {
        executor.submit {
            Log.e(spaceTag, any.toString())
        }
    }

    fun info(any: Any?) {
        executor.submit {
            Log.i(spaceTag, any.toString())
        }
    }

    fun debug(any: Any?) {
        Log.d(spaceTag, any.toString())
    }

    fun warning(any: Any?) {
        executor.submit {
            Log.w(spaceTag, any.toString())
//        if(any != null) {
//            log2File?.log("W:$any")
//        }
        }
    }
}