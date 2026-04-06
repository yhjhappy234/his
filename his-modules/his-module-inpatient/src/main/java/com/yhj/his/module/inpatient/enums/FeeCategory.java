package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 费用分类枚举
 */
@Getter
@AllArgsConstructor
public enum FeeCategory {

    BED("床位", "床位费"),
    DRUG("药品", "药品费"),
    EXAMINATION("检查", "检查费"),
    LAB_TEST("检验", "检验费"),
    TREATMENT("治疗", "治疗费"),
    NURSING("护理", "护理费"),
    MATERIAL("材料", "材料费");

    private final String code;
    private final String description;
}