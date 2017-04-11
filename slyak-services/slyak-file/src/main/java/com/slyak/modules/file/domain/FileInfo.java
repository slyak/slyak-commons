package com.slyak.modules.file.domain;

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