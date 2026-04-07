package com.yhj.his.module.pacs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * PACS模块配置类
 */
@Configuration
@ComponentScan(basePackages = "com.his.module.pacs")
@EnableJpaRepositories(basePackages = "com.his.module.pacs.repository")
public class PacsModuleConfig {
}