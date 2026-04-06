package com.yhj.his.module.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 系统管理模块 - 独立启动器
 *
 * 用于微服务部署，端口: 8081
 *
 * @author HIS Team
 */
@SpringBootApplication
@EnableCaching
@EntityScan("com.yhj.his.module.system.entity")
@EnableJpaRepositories("com.yhj.his.module.system.repository")
public class SystemModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemModuleApplication.class, args);

        System.out.println();
        System.out.println("========================================");
        System.out.println("   HIS系统管理模块启动成功！");
        System.out.println("   访问地址: http://localhost:8081");
        System.out.println("   API文档: http://localhost:8081/swagger-ui.html");
        System.out.println("========================================");
        System.out.println();
    }
}