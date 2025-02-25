package com.cryo.core

import org.hjson.JsonObject
import org.hjson.JsonValue
import java.util.*

class Node(parent: Node?, val name: String, val value: JsonObject) {
    private val attrSet = hashMapOf<String, JsonValue>()
    private val children = LinkedList<Node>()
    var isAttribute: Boolean = false
    init {
        parent?.children?.add(this)
    }

    fun forEach(action: (Int, Node) -> Unit) {
        children.forEachIndexed(action)
    }

    val childCount: Int get() = children.size

    fun attribute(attr: JsonObject.Member) {
        attrSet[attr.name] = attr.value
    }

    fun attribute(name: String, value: JsonValue) {
        attrSet[name] = value
    }

    fun attributes() = attrSet

    fun attribute(name: String): JsonValue? = attrSet[name]

    fun findNodeById(id: String): Node? {
        return children.find { it.attrSet["id"]?.asString() == id }
    }
}