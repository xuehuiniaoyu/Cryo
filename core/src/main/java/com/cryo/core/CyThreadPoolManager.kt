package com.cryo.core

import java.util.concurrent.Executors

object CyThreadPoolManager {
    val pool = Executors.newCachedThreadPool()!!
}