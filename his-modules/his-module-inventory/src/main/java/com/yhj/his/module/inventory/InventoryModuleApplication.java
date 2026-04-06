package com.yhj.his.module.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 库存物资模块 - 独立启动器
 *
 * 用于微服务部署，端口: 8088
 *
 * @author HIS Team
 */
@SpringBootApplication
@EnableCaching
@EntityScan("com.yhj.his.module.inventory.entity")
@EnableJpaRepositories("com.yhj.his.module.inventory.repository")
public class InventoryModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryModuleApplication.class, args);

        System.out.println();
        System.out.println("========================================");
        System.out.println("   HIS库存物资模块启动成功！");
        System.out.println("   访问地址: http://localhost:8088");
        System.out.println("   API文档: http://localhost:8088/swagger-ui.html");
        System.out.println("========================================");
        System.out.println();
    }
}