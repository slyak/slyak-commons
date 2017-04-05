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

package com.slyak.core.support.cloud;

/**
 * 服务发现.
 *
 * @author stormning on 2016/12/2.
 */
public interface ServiceRegistry {
	String REMOTE_CONFIG_KEY = "_remote_cfg";

	<T> T lookup(String serviceId, Class<T> interfaceClass) throws Exception;

	<T> T lookup(String host, int port, Class<T> interfaceClass) throws Exception;

	Protocol getProtocol();
}
