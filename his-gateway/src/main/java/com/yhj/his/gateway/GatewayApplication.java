package com.yhj.his.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HIS API网关启动器
 *
 * @author HIS Team
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);

        System.out.println();
        System.out.println("========================================");
        System.out.println("   HIS API网关启动成功！");
        System.out.println("   访问地址: http://localhost:8080");
        System.out.println("========================================");
        System.out.println();
    }
}