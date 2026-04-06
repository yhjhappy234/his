package com.yhj.his.module.emr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缺陷类型枚举
 */
@Getter
@AllArgsConstructor
public enum DefectType {

    MISSING_FIELD("缺项", 10),
    TIME_LIMIT("时限超时", 5),
    INCOMPLETE_CONTENT("内容不完整", 5),
    FORMAT_ERROR("格式不规范", 3),
    LOGIC_ERROR("逻辑错误", 10);

    private final String description;
    private final int defaultDeduction;
}