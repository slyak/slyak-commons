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

import org.springframework.remoting.caucho.HessianProxyFactoryBean;

/**
 * .
 *
 * @author stormning on 2017/1/13.
 */
public class HessianServiceRegistry extends AbstractServiceRegistry {

	@Override
	@SuppressWarnings("unchecked")
	protected <T> T getClientProxy(String serviceUrl, Class<T> interfaceClass) {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		factory.setServiceUrl(serviceUrl);
		factory.setServiceInterface(interfaceClass);
		factory.afterPropertiesSet();
		return (T) factory.getObject();
	}

	@Override
	public Protocol getProtocol() {
		return Protocol.HESSIAN;
	}
}
