package com.yhj.his.module.lis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 检验管理模块 - 独立启动器
 *
 * 用于微服务部署，端口: 8085
 *
 * @author HIS Team
 */
@SpringBootApplication
@EnableCaching
@EntityScan("com.yhj.his.module.lis.entity")
@EnableJpaRepositories("com.yhj.his.module.lis.repository")
public class LisModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(LisModuleApplication.class, args);

        System.out.println();
        System.out.println("========================================");
        System.out.println("   HIS检验管理模块启动成功！");
        System.out.println("   访问地址: http://localhost:8085");
        System.out.println("   API文档: http://localhost:8085/swagger-ui.html");
        System.out.println("========================================");
        System.out.println();
    }
}