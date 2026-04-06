package com.yhj.his.module.hr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 员工状态枚举
 */
@Getter
@AllArgsConstructor
public enum EmployeeStatus {

    ON_JOB("在职", "在职状态"),
    RESIGNED("离职", "离职状态"),
    SUSPENDED("停职", "停职状态"),
    RETIRED("退休", "退休状态");

    private final String name;
    private final String description;
}