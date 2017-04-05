package com.slyak.core.util

import org.apache.commons.lang3.RandomStringUtils

/**
 * .
 *
 * @author  stormning on 2017/1/11.
 *
 */

object Strings {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    fun randomLetters(len: Int): String? {
        return RandomStringUtils.random(len, chars)
    }
}

fun transform(color: String): Int {
    return when (color) {
        "Red" -> 0
        "Green" -> 1
        "Blue" -> 2
        else -> throw IllegalArgumentException("Invalid color param value")
    }
}

fun main(args: Array<String>) {

    val b: Boolean? = null
    if (b == true) {

    } else {
        // `b` is false or null
    }
}