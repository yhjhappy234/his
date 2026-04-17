package com.yhj.his;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * HIS医院信息系统 - 单体应用启动器
 *
 * 一次性加载所有模块，适用于演示和快速部署
 *
 * @author HIS Team
 */
@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {
    "com.yhj.his.module.system",
    "com.yhj.his.module.outpatient",
    "com.yhj.his.module.inpatient",
    "com.yhj.his.module.pharmacy",
    "com.yhj.his.module.lis",
    "com.yhj.his.module.pacs",
    "com.yhj.his.module.finance",
    "com.yhj.his.module.inventory",
    "com.yhj.his.module.hr",
    "com.yhj.his.module.emr",
    "com.yhj.his.module.voice",
    "com.yhj.his.common"
}, excludeFilters = @ComponentScan.Filter(
    type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
    classes = {
        com.yhj.his.module.system.SystemModuleApplication.class,
        com.yhj.his.module.outpatient.OutpatientModuleApplication.class,
        com.yhj.his.module.inpatient.InpatientModuleApplication.class,
        com.yhj.his.module.pharmacy.PharmacyModuleApplication.class,
        com.yhj.his.module.lis.LisModuleApplication.class,
        com.yhj.his.module.pacs.PacsModuleApplication.class,
        com.yhj.his.module.finance.FinanceModuleApplication.class,
        com.yhj.his.module.inventory.InventoryModuleApplication.class,
        com.yhj.his.module.hr.HrModuleApplication.class,
        com.yhj.his.module.emr.EmrModuleApplication.class,
        com.yhj.his.module.voice.VoiceModuleApplication.class
    }
))
@EntityScan(basePackages = {
    "com.yhj.his.module.system.entity",
    "com.yhj.his.module.outpatient.entity",
    "com.yhj.his.module.inpatient.entity",
    "com.yhj.his.module.pharmacy.entity",
    "com.yhj.his.module.lis.entity",
    "com.yhj.his.module.pacs.entity",
    "com.yhj.his.module.finance.entity",
    "com.yhj.his.module.inventory.entity",
    "com.yhj.his.module.hr.entity",
    "com.yhj.his.module.emr.entity",
    "com.yhj.his.module.voice.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.yhj.his.module.system.repository",
    "com.yhj.his.module.outpatient.repository",
    "com.yhj.his.module.inpatient.repository",
    "com.yhj.his.module.pharmacy.repository",
    "com.yhj.his.module.lis.repository",
    "com.yhj.his.module.pacs.repository",
    "com.yhj.his.module.finance.repository",
    "com.yhj.his.module.inventory.repository",
    "com.yhj.his.module.hr.repository",
    "com.yhj.his.module.emr.repository",
    "com.yhj.his.module.voice.repository"
})
public class HisApplication {

    public static void main(String[] args) {
        SpringApplication.run(HisApplication.class, args);

        System.out.println();
        System.out.println("========================================");
        System.out.println("   HIS医院信息系统启动成功！");
        System.out.println("   访问地址: http://localhost:8080");
        System.out.println("   API文档: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
        System.out.println();
    }
}