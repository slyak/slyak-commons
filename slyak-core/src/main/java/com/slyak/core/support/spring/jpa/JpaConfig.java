package com.slyak.core.support.spring.jpa;

import com.slyak.spring.jpa.GenericJpaRepositoryFactoryBean;
import com.slyak.spring.jpa.GenericJpaRepositoryImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * .
 *
 * @author stormning on 2016/12/17.
 */
@Configuration
@EnableJpaRepositories(basePackages = "${slyak.core.jpa.basePackages:com.slyak}", repositoryBaseClass = GenericJpaRepositoryImpl.class, repositoryFactoryBeanClass = GenericJpaRepositoryFactoryBean.class)
@EntityScan({ "${slyak.core.jpa.basePackages:com.slyak}" })
public class JpaConfig {

}
