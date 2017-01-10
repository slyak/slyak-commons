package com.slyak.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * .
 *
 * @author stormning on 2017/1/3.
 */
@ConfigurationProperties(prefix = "slyak.core")
public class ConfigProperties {

	private Jpa jpa = new Jpa();

	@Data
	public class Jpa {
		private String basePackages;
	}
}
