package com.yhj.his.module.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 财务收费模块 - 独立启动器
 *
 * 用于微服务部署，端口: 8087
 *
 * @author HIS Team
 */
@SpringBootApplication
@EnableCaching
@EntityScan("com.yhj.his.module.finance.entity")
@EnableJpaRepositories("com.yhj.his.module.finance.repository")
public class FinanceModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceModuleApplication.class, args);

        System.out.println();
        System.out.println("========================================");
        System.out.println("   HIS财务收费模块启动成功！");
        System.out.println("   访问地址: http://localhost:8087");
        System.out.println("   API文档: http://localhost:8087/swagger-ui.html");
        System.out.println("========================================");
        System.out.println();
    }
}