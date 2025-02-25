package com.cryo

import org.hjson.JsonValue
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testHJson() {
        val jsonValue = JsonValue.readHjson("""
            {
               mappings: {
                  ui1: {
                     class:"com.cryo.Component1"
                  }
               }
            }
        """.trimIndent())
        println(jsonValue)
    }
}