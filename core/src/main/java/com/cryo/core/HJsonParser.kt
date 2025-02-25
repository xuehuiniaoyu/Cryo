package com.cryo.core

import org.hjson.JsonObject
import org.hjson.JsonValue
import java.io.*

open class HJsonParser {
    private var input: Reader? = null
    private lateinit var inflaterStrategy: InflaterStrategy

    interface InflaterStrategy {
        fun inflater(): JsonObject
    }

    fun open(file: File): HJsonParser {
        input = InputStreamReader(FileInputStream(file))
        inflaterStrategy = object: InflaterStrategy {
            override fun inflater(): JsonObject {
                return JsonValue.readHjson(input).asObject()
            }
        }
        return this
    }

    fun open(inputStream: InputStream): HJsonParser {
        input = InputStreamReader(inputStream)
        inflaterStrategy = object: InflaterStrategy {
            override fun inflater(): JsonObject {
                return JsonValue.readHjson(input).asObject()
            }
        }
        return this
    }

    fun open(jsonValue: JsonValue): HJsonParser {
        inflaterStrategy = object: InflaterStrategy {
            override fun inflater(): JsonObject {
                return jsonValue.asObject()
            }
        }
        return this
    }

    fun close() {
        input?.close()
        input = null
        println("close.")
    }

    private fun dispatchValues(node: Node, member: JsonObject.Member) {
        if(!node.isAttribute && member.value.isObject) {
            inflater(member.name, member.value.asObject(), node)
        }
        else /*if (isAttr(member.name))*/ {
            node.attribute(member)
        }
    }

    private fun inflater(name: String, jsonObject: JsonObject, parent: Node?): Node {
        val node = Node(parent, name, jsonObject)
        println("inflater:$name -> ${jsonObject.size()}")
        jsonObject.forEach { m0 ->
            println("inflater:--- ${m0.name}")
            dispatchValues(node, m0)
        }
        return node
    }

    fun inflater(): Node {
        try {
            val document = inflaterStrategy.inflater()
            return inflater("body", document, null)
        } finally {
            close()
        }
    }
}