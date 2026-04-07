package com.yhj.his.module.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 就诊类型枚举
 */
@Getter
@AllArgsConstructor
public enum VisitType {

    OUTPATIENT("门诊", "门诊就诊"),
    INPATIENT("住院", "住院就诊"),
    EMERGENCY("急诊", "急诊就诊"),
    PHYSICAL_EXAM("体检", "健康体检");

    private final String code;
    private final String description;
}