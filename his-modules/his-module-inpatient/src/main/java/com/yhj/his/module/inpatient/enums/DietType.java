package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 饮食类型枚举
 */
@Getter
@AllArgsConstructor
public enum DietType {

    NORMAL("普食", "普通饮食"),
    SOFT("软食", "软食"),
    LIQUID("流食", "流质饮食"),
    SEMI_LIQUID("半流食", "半流质饮食"),
    FASTING("禁食", "禁食");

    private final String code;
    private final String description;
}