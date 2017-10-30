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

package com.slyak.services.file.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
//import com.slyak.spring.jpa.hibernate.JSONType;
import lombok.Data;
import lombok.SneakyThrows;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.InputStream;
import java.util.Map;

/**
 * .
 *
 * @author stormning on 2017/1/4.
 */
@Data
@Entity
@Table(name = "t_file_info", indexes = @Index(columnList = "key"))
public class FileInfo extends AbstractAuditable<Long, Long> {

	@Transient
	@JsonIgnore
	private final InputStream inputStream;

	private String key;

	private String contentType;

	private String name;

	private long size;

	/*file owner*/
	private String owner;

//	@Type(type = JSONType.TYPE)
	private Map<String, String> meta = Maps.newHashMap();

	@SneakyThrows
	public FileInfo(MultipartFile file) {
		this.size = file.getSize();
		this.name = file.getName();
		this.contentType = file.getContentType();
		this.inputStream = file.getInputStream();
	}
}