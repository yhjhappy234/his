package com.yhj.his.module.pharmacy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 就诊类型枚举
 */
@Getter
@AllArgsConstructor
public enum VisitType {

    OUTPATIENT("门诊", "门诊就诊"),
    INPATIENT("住院", "住院就诊");

    private final String name;
    private final String description;
}