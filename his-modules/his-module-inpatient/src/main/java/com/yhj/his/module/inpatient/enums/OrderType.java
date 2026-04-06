package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 医嘱类型枚举
 */
@Getter
@AllArgsConstructor
public enum OrderType {

    LONG_TERM("长期", "持续执行的医嘱"),
    TEMPORARY("临时", "一次性执行的医嘱"),
    DISCHARGE("出院", "出院时执行的医嘱");

    private final String code;
    private final String description;
}