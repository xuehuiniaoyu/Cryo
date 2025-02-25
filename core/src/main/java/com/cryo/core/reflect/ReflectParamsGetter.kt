package com.cryo.core.reflect

object ReflectParamsGetter {
    fun <T: Any> get(targetObj: Any?, fieldName: String): T? {
        if(fieldName == "") return targetObj as? T
        if(targetObj == null) return null
        return if(fieldName.contains(".")) {
            val name = fieldName.substringBefore(".")
            get(ReflectUtil.ReflectClass(targetObj).getField(name), fieldName.substringAfter(name.plus(".")))
        }
        else {
            val fieldValue: Any? = ReflectUtil.ReflectClass(targetObj).getField(fieldName)
            fieldValue as? T
        }
    }

    fun getName(name: String): String = if(name.contains(".")) name.substringBefore(".") else name
    fun getChildrenName(name: String) = if(name.contains(".")) name.substringAfter(".") else ""
}