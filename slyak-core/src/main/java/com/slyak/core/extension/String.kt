package com.slyak.core.extension

import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import java.util.*

/**
 * .
 *
 * @author  stormning on 2017/1/12.
 *
 */
fun String.toCamelCase(): String {
    val buf = StringBuilder(this.replace('.', '_'))
    var i = 1
    while (i < buf.length - 1) {
        buf[i - 1].isLowerCase()
        if (buf[i - 1].isLowerCase() && buf[i].isUpperCase() && buf[i + 1].isLowerCase()) {
            buf.insert(i++, '_')
        }
        i++
    }
    return buf.toString().toLowerCase(Locale.ROOT)
}

fun String.camelToProperty(): String {
    return ""
}

fun String.random(len: Int): String {
    return RandomStringUtils.random(len, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")!!
}

fun String.parseDate(pattern: String): Date {
    return DateUtils.parseDate(this, pattern)
}

fun StringUtils.randomLetter(len: Int) = RandomStringUtils.random(len, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")!!