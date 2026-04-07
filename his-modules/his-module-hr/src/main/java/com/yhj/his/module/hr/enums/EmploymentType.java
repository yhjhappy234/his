package com.yhj.his.module.hr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 员工用工类型枚举
 */
@Getter
@AllArgsConstructor
public enum EmploymentType {

    FULL_TIME("全职", "全职员工"),
    PART_TIME("兼职", "兼职员工"),
    TEMPORARY("临时", "临时员工"),
    INTERN("实习", "实习员工");

    private final String name;
    private final String description;
}