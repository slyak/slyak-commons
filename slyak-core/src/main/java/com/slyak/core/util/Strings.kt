package com.slyak.core.util

import com.slyak.core.extension.parseDate
import com.slyak.core.extension.randomLetter
import org.apache.commons.lang3.StringUtils

/**
 * .
 *
 * @author  stormning on 2017/1/11.
 *
 */

object Test{
    fun a()="asd"
}

fun main(args: Array<String>) {
//    var map = mapOf(Pair("a", "b"), Pair("c", "d"))

    Test.a()

    var map = mapOf("a" to "b", "c" to "d")
    for ((k, v) in map) {
        println("key:$k,value:$v")
    }

    for (i in 1..10) {
        println(i)
    }

    fun String.spaceToCamelCase(): String {
        return this.toLowerCase()
    }

    println(StringUtils().randomLetter(4))

    println("2012/09/09".parseDate("yyyy/MM/dd"))
}
