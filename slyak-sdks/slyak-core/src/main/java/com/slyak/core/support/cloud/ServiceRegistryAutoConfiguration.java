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

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * .
 *
 * @author stormning on 2016/12/6.
 */
@Configuration
@EnableConfigurationProperties(ServiceRegistryProperties.class)
public class ServiceRegistryAutoConfiguration {

	@Autowired
	private ServiceRegistryProperties registryProperties;

	@Autowired(required = false)
	private ServiceIdProvider serviceIdProvider;

	@Bean
	public ServiceRegistryBeanPostProcessor registryBeanPostProcessor() throws Exception {
		ServiceRegistryBeanPostProcessor processor = new ServiceRegistryBeanPostProcessor();
		processor.setServiceNameProvider(serviceNameProvider());
		processor.setRegistryProperties(registryProperties);
		return processor;
	}

	@Bean
	public HessianServiceRegistry serviceRegistry() {
		HessianServiceRegistry serviceRegistry = new HessianServiceRegistry();
		serviceRegistry.setServiceNameProvider(serviceNameProvider());
		return serviceRegistry;
	}

	/*@Bean
	public RMIServiceRegistry serviceRegistry() {
		RMIServiceRegistry serviceRegistry = new RMIServiceRegistry();
		serviceRegistry.setServiceNameProvider(serviceNameProvider());
		return serviceRegistry;
	}*/

	@Bean
	public ServiceNameProvider serviceNameProvider() {
		return new DefaultServiceNameProvider();
	}

	@Bean
	public EurekaInstanceConfigEnhancer enhancerProcessor() {
		EurekaInstanceConfigEnhancer processor = new EurekaInstanceConfigEnhancer();
		processor.setMetaDataEnhancers(Lists.<MetaDataEnhancer>newArrayList(rmiMetaDataEnhancer()));
		processor.setServiceIdProvider(serviceIdProvider);
		return processor;
	}

	@Bean
	public RmiMetaDataEnhancer rmiMetaDataEnhancer() {
		RmiMetaDataEnhancer dataEnhancer = new RmiMetaDataEnhancer();
		dataEnhancer.setProperties(registryProperties);
		return dataEnhancer;
	}
}
