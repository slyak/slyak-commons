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

package com.slyak.services

/**
 * 缓存服务.
 *
 * @author stormning 2017/4/28
 * @since 1.3.0
 */

interface CacheService {
    /**
     * 设置缓存
     * @param key 键
     * @param
     * @param ttl 发呆时间
     */
    fun put(key: String, value: Any, ttl: Long? = -1)

    /**
     * 获取缓存
     * @param key 键
     */
    fun <T> get(key: String): T

    /**
     * 命中一下，如果允许的话返回true
     * @param key 键
     * @param maxHit 最大命中数
     * @param period 时间段
     */
    fun hit(key: String, maxHit: Int, period: Long): Boolean
}