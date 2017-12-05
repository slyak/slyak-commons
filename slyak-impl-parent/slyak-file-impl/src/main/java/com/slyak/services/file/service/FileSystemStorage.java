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

package com.slyak.services.file.service;

import java.io.File;
import java.io.InputStream;

/**
 * .
 *
 * @author stormning on 2017/1/6.
 */
public class FileSystemStorage implements StorageService {

	private String storePath;

	public static void main(String[] args) {
	}

	@Override
	public String store(InputStream inputStream) {
		String key = "";

		return key;
	}

	@Override
	public File restore(String key) {
		return null;
	}
}
