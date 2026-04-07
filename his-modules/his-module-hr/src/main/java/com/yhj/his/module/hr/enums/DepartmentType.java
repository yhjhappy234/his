package com.yhj.his.module.hr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 科室类型枚举
 */
@Getter
@AllArgsConstructor
public enum DepartmentType {

    CLINICAL("临床", "临床科室"),
    MEDICAL_TECH("医技", "医技科室"),
    ADMIN("行政", "行政科室"),
    LOGISTICS("后勤", "后勤科室");

    private final String name;
    private final String description;
}