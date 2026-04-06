package com.yhj.his.module.emr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 电子病历模块 - 独立启动器
 *
 * 用于微服务部署，端口: 8090
 *
 * @author HIS Team
 */
@SpringBootApplication
@EnableCaching
@EntityScan("com.yhj.his.module.emr.entity")
@EnableJpaRepositories("com.yhj.his.module.emr.repository")
public class EmrModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmrModuleApplication.class, args);

        System.out.println();
        System.out.println("========================================");
        System.out.println("   HIS电子病历模块启动成功！");
        System.out.println("   访问地址: http://localhost:8090");
        System.out.println("   API文档: http://localhost:8090/swagger-ui.html");
        System.out.println("========================================");
        System.out.println();
    }
}