package com.cryo.core.reflect

import java.lang.reflect.Field
import java.lang.reflect.Method

fun Class<*>.isSuper(clazz: Class<*>?): Boolean {
    return when (clazz) {
        null -> false
        this -> true
        else -> clazz.interfaces.find { isSuper(it) } != null || isSuper(clazz.superclass)
    }
}

object ReflectUtil {
    private fun conversion(type: Class<*>): Class<*> {
        return when (type) {
            java.lang.Integer::class.java -> Int::class.java
            java.lang.Boolean::class.java -> Boolean::class.java
            else -> type
        }
    }

    class ReflectClass(private val obj: Any) {
        private val clazz = obj.javaClass

        fun field(name: String): Field? {
            return clazz.getDeclaredField(name)
        }

        fun <F : Any> getField(name: String): F? {
            return try {
                clazz.getDeclaredField(name).also { it.isAccessible = true }.get(obj) as? F
            } catch (ex: NoSuchFieldException) {
                // kotlin自动把 val s: String get() = xxx 这种方法转成Method
                // bool类型则不转
                val method = try {
                    clazz.getDeclaredMethod(name)
                } catch (methodNoSuchEx: NoSuchMethodException) {
                    val getMethodName = "get".plus(name.substring(0, 1).uppercase()).plus(name.substring(1))
                    clazz.getDeclaredMethod(getMethodName)
                }
                method.invoke(obj) as? F
            }
        }

        fun setField(name: String, value: Any?) {
            println("set field ${value?.javaClass}")
            val setMethodName = "set".plus(name.substring(0, 1).uppercase()).plus(name.substring(1))
            clazz.declaredMethods.find {
                it.name == setMethodName && it.parameterTypes.find { clz ->
                    clz.isSuper(value?.javaClass)
                } != null
            }?.invoke(obj, value) ?: clazz.getDeclaredField(name).also { it.isAccessible = true }.set(obj, value)
        }

        fun <M : Any> getMethod(name: String, types: Array<Class<*>>?, vararg args: Any): M? {
            return clazz.getDeclaredMethod(
                name,
                *(types ?: args.map { conversion(it.javaClass) }.toTypedArray())
            ).invoke(obj, *args) as? M
        }

        fun getDeclaredMethod(name: String, types: Array<Class<*>>): Method? {
            return clazz.getDeclaredMethod(
                name,
                *types
            )
        }
    }
}