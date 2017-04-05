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

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

import javax.naming.ServiceUnavailableException;
import java.util.Map;

/**
 * Use ribbon's load balancer client.
 *
 * @author stormning on 2016/12/2.
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry, ServiceUrlProvider {

	@Value("${server.ssl.enabled:false}")
	private boolean sslEnable = false;

	@Autowired(required = false)
	private LoadBalancerClient loadBalancerClient;

	private Map<String, Object> clientCache = Maps.newConcurrentMap();

	private ServiceNameProvider serviceNameProvider;

	@Override
	public <T> T lookup(String serviceId, Class<T> interfaceClass) throws Exception {
		ServiceInstance instance = loadBalancerClient.choose(serviceId);
		if (instance != null) {
			return lookup(instance.getHost(), getPort(instance), interfaceClass);
		}
		throw new ServiceUnavailableException("Exporter Unavailable :" + interfaceClass + " under " + serviceId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T lookup(String host, int port, Class<T> interfaceClass) throws Exception {
		String serviceUrl = getServiceUrl(host, port, interfaceClass);
		Object proxy = clientCache.get(serviceUrl);
		if (proxy == null) {
			proxy = getClientProxy(serviceUrl, interfaceClass);
			if (proxy != null) {
				clientCache.put(serviceUrl, proxy);
			}
		}
		return (T) proxy;
	}

	protected int getPort(ServiceInstance instance) {
		String remoteCfg = instance.getMetadata().get(REMOTE_CONFIG_KEY);
		RemoteConfig remoteConfig = JSON.parseObject(remoteCfg, RemoteConfig.class);
		return remoteConfig.getPort();
	}

	protected abstract <T> T getClientProxy(String serviceUrl, Class<T> interfaceClass);

	@Override
	public String getServiceUrl(String host, int port, Class<?> interfaceClass) {
		String schema = getProtocol().getUrlPrefix();
		if ((getProtocol() == Protocol.HESSIAN) && sslEnable) {
			schema += "s";
		}
		return schema + "://" + host + ":" + port + "/" +
				getServiceNameProvider().getServiceName(interfaceClass, null);
	}

	public ServiceNameProvider getServiceNameProvider() {
		return serviceNameProvider;
	}

	public void setServiceNameProvider(ServiceNameProvider serviceNameProvider) {
		this.serviceNameProvider = serviceNameProvider;
	}
}