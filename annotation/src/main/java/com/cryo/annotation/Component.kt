package com.cryo.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Component(
    val name: String,
    val isMain: Boolean = false,
    val path: String = "#",
    val theme: String = "#",
    val extras: Array<String> = []
)