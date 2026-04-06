package com.yhj.his.module.hr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 请假类型枚举
 */
@Getter
@AllArgsConstructor
public enum LeaveType {

    PERSONAL("事假", "个人事务请假"),
    SICK("病假", "因病请假"),
    ANNUAL("年假", "年度休假"),
    MATERNITY("产假", "女职工产假"),
    MARRIAGE("婚假", "结婚休假"),
    BEREAVEMENT("丧假", "丧事请假"),
    OFFICIAL("公假", "公务请假");

    private final String name;
    private final String description;
}