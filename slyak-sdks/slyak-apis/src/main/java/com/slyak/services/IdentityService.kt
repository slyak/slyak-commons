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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

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
 * 修改状态请求
 */
open class ChangeStatusReq {
    var userIds: Set<Long> = null!!
    var status: Int = null!!
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
    //编号
    var id: Long? = null

    //真实姓名
    var realName: String? = null

    //状态
    var status: Int = 0
}

/**
 * 用户
 */
class User : BaseUser() {
    var password: String = null!!
}

enum class AccountType(val title: String) {
    MAIL("邮箱"),
    QQ("QQ"),
    WEI_BO("微博"),
    WEI_XIN("微信"),
    GIT_HUB("GitHub")
}

/**
 * 一个用户多个类型账号
 */
class Account() {
    //用户编号
    var userId: Long = null!!
    //登陆名
    var loginName: String? = null
    //凭证或密码
    var token: String = null!!
    //账号类型
    var type: AccountType = AccountType.MAIL
    //昵称
    var nickName: String = null!!
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
    var accounts: List<Account> = null!!
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
    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/login")
    fun login(@RequestBody req: LoginReq): String

    /**
     * 停用
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/updateUserStatus")
    fun updateUserStatus(@RequestBody req: ChangeStatusReq)

    /**
     * 获取用户详情
     */
    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/userDetails")
    fun getUserDetails(@RequestParam userIds: Set<Long>): Map<Long, UserDetail>

    /**
     * 获取用户详情
     */
    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/userDetail/{userId}")
    fun getUserDetail(@RequestParam userId: Long): UserDetail

    /**
     * 创建用户
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/user")
    fun createUser(@RequestBody user: User): User

    /**
     * 修改用户密码
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/safeChangePwd")
    fun safeChangePwd(@RequestBody req: SafeChangePwdReq)

    /**
     * 直接修改用户密码
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/changePwd")
    fun changePwd(@RequestBody req: ChangePwdReq)


}