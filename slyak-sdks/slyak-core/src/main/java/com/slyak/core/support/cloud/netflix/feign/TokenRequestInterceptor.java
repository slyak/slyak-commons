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

package com.slyak.core.support.cloud.netflix.feign;

import feign.RequestTemplate;
import org.springframework.cloud.netflix.feign.encoding.BaseRequestInterceptor;
import org.springframework.cloud.netflix.feign.encoding.FeignClientEncodingProperties;
import org.springframework.cloud.netflix.feign.encoding.HttpEncoding;

/**
 * .
 *
 * @author stormning 2017/5/2
 * @since 1.3.0
 */
public class TokenRequestInterceptor extends BaseRequestInterceptor {

	/**
	 * Creates new instance of {@link BaseRequestInterceptor}.
	 *
	 * @param properties the encoding properties
	 */
	protected TokenRequestInterceptor(
			FeignClientEncodingProperties properties) {
		super(properties);
	}

	@Override
	public void apply(RequestTemplate template) {
		addHeader(template, HttpEncoding.CONTENT_ENCODING_HEADER, HttpEncoding.GZIP_ENCODING,
				HttpEncoding.DEFLATE_ENCODING);
	}
}
