package com.cryo.core

/**
 * 全局变量，你可以根据自己的需求添加
 * 当插件有需要用到全局变量中的变量是就会通过get方法获取
 * 注意这里最好添加静态变量
 */
object COMMON {
    data class ObjInfo(val value: Any, val type: Int)
    private val queue by lazy { hashMapOf<String, Any>() }
    fun set(key: String, value: Any) {
        queue[key] = value
    }

    fun get(key: String): Any? {
        return queue[key]
    }
}