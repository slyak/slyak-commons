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

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * TODO
 * enhance meta data with providers.
 *
 * @author stormning on 2016/12/6.
 */
@Data
public class EurekaInstanceConfigEnhancer implements InitializingBean {

	private List<MetaDataEnhancer> metaDataEnhancers;

	private ServiceIdProvider serviceIdProvider;

	@Autowired(required = false)
	private EurekaInstanceConfigBean instanceConfig;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (instanceConfig != null) {
			Map<String, String> metadataMap = instanceConfig.getMetadataMap();
			if (metadataMap == null) {
				metadataMap = Maps.newHashMap();
				instanceConfig.setMetadataMap(metadataMap);
			}
			for (MetaDataEnhancer enhancer : metaDataEnhancers) {
				enhancer.enhance(metadataMap);
			}

			if (serviceIdProvider != null) {
				String currentServiceId = serviceIdProvider.getCurrentServiceId();
				if (StringUtils.hasText(currentServiceId)) {
					instanceConfig.setAppname(currentServiceId);
					instanceConfig.setVirtualHostName(currentServiceId);
					instanceConfig.setSecureVirtualHostName(currentServiceId);
				}
			}
		}
	}
}