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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.remoting.rmi.RmiServiceExporter;

/**
 * export service after bean initialed.
 *
 * @author stormning on 2016/12/6.
 */
@Data
@Slf4j
public class ServiceRegistryBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	private ServiceNameProvider serviceNameProvider = new DefaultServiceNameProvider();

	private ServiceRegistryProperties registryProperties;

	private Protocol protocol;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class beanClazz = bean.getClass();
		Exporter service = AnnotationUtils.getAnnotation(beanClazz, Exporter.class);
		try {
			if (service != null) {
				DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
						.getAutowireCapableBeanFactory();
				//export rmi service
				Class<?>[] interfaces = service.interfaces();
				for (Class<?> aClass : interfaces) {
					checkInterfaceExist(beanClazz, aClass);
					Object exporter = null;
					String serviceName = serviceNameProvider.getServiceName(aClass, null);
					switch (protocol) {
					case RMI:
						RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
						rmiServiceExporter.setServiceName(serviceName);
						rmiServiceExporter.setServiceInterface(aClass);
						rmiServiceExporter.setRegistryPort(registryProperties.getPort());
						rmiServiceExporter.setService(bean);

						//rmiServiceExporter.setServerSocketFactory(new SslRMIServerSocketFactory());
						//rmiServiceExporter.setClientSocketFactory(new SslRMIClientSocketFactory());

						exporter = rmiServiceExporter;
						break;
					case HESSIAN:
						HessianServiceExporter hessianServiceExporter = new HessianServiceExporter();
						hessianServiceExporter.setService(bean);
						hessianServiceExporter.setServiceInterface(aClass);
						serviceName = "/" + serviceName;
						exporter = hessianServiceExporter;
						//more protocol support
						break;
					default:
						break;
					}
					beanFactory.initializeBean(exporter, serviceName);
					beanFactory.registerSingleton(serviceName, exporter);

				}
			}
		}
		catch (Exception e) {
			log.error("Export service error", e);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ServiceRegistry registry = applicationContext.getBean(ServiceRegistry.class);
		this.protocol = registry.getProtocol();
	}

	//check bean`s interface exist.
	private void checkInterfaceExist(Class<?> beanClazz, Class<?> interfaceClazz) {
		Class<?>[] interfaces = beanClazz.getInterfaces();
		boolean exist = false;
		for (int i = 0; i < interfaces.length; i++) {
			if (interfaces[i].equals(interfaceClazz)) {
				exist = true;
				break;
			}
		}
		if (!exist) {
			throw new RuntimeException(
					"can not found interface: bean:" + beanClazz.getName() + " i:" + interfaceClazz.getName());
		}
	}

}
