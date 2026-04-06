package com.yhj.his.module.voice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 语音呼叫模块 - 独立启动器
 *
 * 用于微服务部署，端口: 8091
 *
 * @author HIS Team
 */
@SpringBootApplication
@EnableCaching
@EntityScan("com.yhj.his.module.voice.entity")
@EnableJpaRepositories("com.yhj.his.module.voice.repository")
public class VoiceModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoiceModuleApplication.class, args);

        System.out.println();
        System.out.println("========================================");
        System.out.println("   HIS语音呼叫模块启动成功！");
        System.out.println("   访问地址: http://localhost:8091");
        System.out.println("   API文档: http://localhost:8091/swagger-ui.html");
        System.out.println("========================================");
        System.out.println();
    }
}