/*
 *  Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.slyak.services.counter

import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * 计数服务.
 *
 * @author stormning 2017/4/28
 * @since 1.3.0
 */
@FeignClient("CounterService")
interface CounterService {
    /**
     * 自增
     * @param key 键
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/{key}")
    fun increase(@PathVariable key: String): Int

    /**
     * 获取当前计数
     */
    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/{key}")
    fun get(@PathVariable key: String): Int


    /**
     * 获取当前计数
     */
    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/{key}")
    fun get(keys: Set<String>): Map<String, Int>

    /**
     * 设置当前计数
     */
    @RequestMapping(method = arrayOf(RequestMethod.PUT), value = "/{key}")
    fun set(@PathVariable key: String, @RequestBody value: Long)

    /**
     * 删除计数
     */
    @RequestMapping(method = arrayOf(RequestMethod.DELETE), value = "/{key}")
    fun flush(@PathVariable key: String)

    /**
     * 删除计数
     */
    @RequestMapping(method = arrayOf(RequestMethod.DELETE), value = "/{keys}")
    fun flush(keys: Array<String>)

    /**
     * 删除全部
     */
    @RequestMapping(method = arrayOf(RequestMethod.DELETE), value = "/")
    fun flushAll()
}