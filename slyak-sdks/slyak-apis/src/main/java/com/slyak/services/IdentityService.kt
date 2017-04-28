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

import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 身份认证服务.
 *
 * @author stormning 2017/4/28
 * @since 1.3.0
 */

/**
 * 登陆请求
 */
class LoginReq {
    var loginName: String = null!!
    var password: String = null!!
}


/**
 * 修改密码请求
 */
open class ChangePwdReq {
    var id: Long = null!!
    var newPwd: String = null!!
}

/**
 * 安全修改密码请求
 */
class SafeChangePwdReq : ChangePwdReq() {
    var oldPwd: String = null!!
}

/**
 * 基本用户
 */
open class BaseUser {
    var id: Long? = null
    /**
     * 登陆名
     */
    var loginName: String = null!!

    /**
     * 状态
     */
    var status: Int = 0
}

/**
 * 用户
 */
class User : BaseUser() {
    var password: String = null!!
}

enum class AccountType(val title: String) {
    PRIMARY("系统主账号"),
    QQ("QQ"),
    WEI_BO("微博"),
    WEI_XIN("微信"),
    GIT_HUB("GitHub")
}

/**
 * 账号
 */
class Account(val type: AccountType = AccountType.PRIMARY) {
    var id: String = null!!
    var name: String = null!!
}

/**
 * 用户详情
 */
class UserDetail : BaseUser() {
    /**
     * 角色列表
     */
    var roles: List<Role>? = null

    /**
     * 账号列表
     */
}

/**
 * 角色
 */
open class Role {
    var id: Long = null!!
    /**
     * 标题
     */
    var title: String = null!!
    /**
     * 操作
     */
    var operations: String? = null
}

@FeignClient("IdentityService")
interface IdentityService {
    /**
     * 登陆
     */
    @ResponseBody
    fun login(@RequestBody req: LoginReq): String

    /**
     * 停用
     */
    @ResponseStatus(HttpStatus.OK)
    fun disableUsers(@RequestParam userIds: Set<Long>)

    /**
     * 启用
     */
    @ResponseStatus(HttpStatus.OK)
    fun enableUsers(@RequestParam userIds: Set<Long>)

    /**
     * 获取用户详情
     */
    @ResponseBody
    fun getUserDetails(@RequestParam userIds: Set<Long>): Map<Long, UserDetail>

    /**
     * 获取用户详情
     */
    @ResponseBody
    fun getUserDetail(@RequestParam userId: Long)

    /**
     * 创建用户
     */
    @ResponseBody
    fun createUser(@RequestBody user: User): User

    /**
     * 修改用户密码
     */
    fun safeChangePwd(req: SafeChangePwdReq)

    /**
     * 直接修改用户密码
     */
    fun changePwd(req: ChangePwdReq)
}