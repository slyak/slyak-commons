package com.slyak.core.util

import com.google.common.hash.Hashing
import com.google.common.io.BaseEncoding
import java.util.*

/**
 * .
 *
 * @author  stormning on 2017/1/6.
 *
 */
object Codes {
    fun md5(input: String) =
            Hashing.md5().hashString(input, charset("UTF-8")).toString()

    fun uuidMd5() = md5(UUID.randomUUID().toString())

    fun base64(input: String) = BaseEncoding.base64().encode(input.toByteArray(Charsets.UTF_8))

    fun base64Decode(base64Input: String) = String(BaseEncoding.base64().decode(base64Input), Charsets.UTF_8)
}


fun main(args: Array<String>) {
    val base64 = Codes.base64("abcdefg«")
    println(base64)
    println(Codes.base64Decode(base64))
}