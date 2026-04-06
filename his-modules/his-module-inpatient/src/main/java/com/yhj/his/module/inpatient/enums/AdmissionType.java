package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 入院类型枚举
 */
@Getter
@AllArgsConstructor
public enum AdmissionType {

    EMERGENCY("急诊", "急诊入院"),
    OUTPATIENT("门诊", "门诊入院"),
    TRANSFER("转院", "转院入院");

    private final String code;
    private final String description;
}