package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 护理等级枚举
 */
@Getter
@AllArgsConstructor
public enum NursingLevel {

    SPECIAL("特级", "24小时专人护理"),
    LEVEL_1("一级", "每小时巡视"),
    LEVEL_2("二级", "每2小时巡视"),
    LEVEL_3("三级", "每3小时巡视");

    private final String code;
    private final String description;
}