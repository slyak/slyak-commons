package com.slyak.core.util

import com.google.common.hash.Hashing
import com.google.common.io.BaseEncoding
import java.nio.charset.Charset
import java.util.*

/**
 * .
 *
 * @author  stormning on 2017/1/11.
 *
 */
object Codes {

    val DEFAULT_ENCODING: Charset = Charsets.UTF_8

    fun md5(input: String) =
            Hashing.md5().hashString(input, DEFAULT_ENCODING).toString()

    fun uuid() = UUID.randomUUID().toString()

    fun uuidMd5() = md5(uuid())

    fun base64(input: String) = BaseEncoding.base64().encode(byte(input))!!

    fun base64Decode(base64Input: String) = String(BaseEncoding.base64().decode(base64Input), DEFAULT_ENCODING)

    private fun byte(input: String) = input.toByteArray(DEFAULT_ENCODING)
}

class Test {
    val p: String by lazy {
        println("p is lazy")
        "tesat"
    }
}

fun main(args: Array<String>) {
    val test = Test()
    println(test.p)
    println(test.p)


    val base64 = Codes.base64("abcdefg«")
    println(base64)
    println(Codes.DEFAULT_ENCODING)
    println(Codes.base64Decode(base64))
    println(Codes.uuidMd5())
}