package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 医嘱分类枚举
 */
@Getter
@AllArgsConstructor
public enum OrderCategory {

    DRUG("药品", "药品医嘱"),
    EXAMINATION("检查", "检查医嘱"),
    LAB_TEST("检验", "检验医嘱"),
    TREATMENT("治疗", "治疗医嘱"),
    NURSING("护理", "护理医嘱"),
    DIET("饮食", "饮食医嘱");

    private final String code;
    private final String description;
}