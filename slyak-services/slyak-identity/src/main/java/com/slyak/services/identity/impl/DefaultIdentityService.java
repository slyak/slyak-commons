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

package com.slyak.services.identity.impl;

import com.slyak.services.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * .
 *
 * @author stormning 2017/5/8
 * @since 1.3.0
 */
public class DefaultIdentityService implements IdentityService {
	@NotNull
	@Override
	public String login(@NotNull LoginReq req) {
		return null;
	}

	@Override
	public void updateUserStatus(@NotNull ChangeStatusReq req) {

	}

	@NotNull
	@Override
	public Map<Long, UserDetail> getUserDetails(@NotNull Set<Long> userIds) {
		return null;
	}

	@NotNull
	@Override
	public UserDetail getUserDetail(long userId) {
		return null;
	}

	@NotNull
	@Override
	public User createUser(@NotNull User user) {
		return null;
	}

	@Override
	public void safeChangePwd(@NotNull SafeChangePwdReq req) {

	}

	@Override
	public void changePwd(@NotNull ChangePwdReq req) {

	}
}
