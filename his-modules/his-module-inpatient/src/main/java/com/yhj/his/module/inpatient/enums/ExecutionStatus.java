package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 执行状态枚举
 */
@Getter
@AllArgsConstructor
public enum ExecutionStatus {

    EXECUTED("已执行", "已执行"),
    SKIPPED("已跳过", "已跳过"),
    ABNORMAL("异常", "执行异常");

    private final String code;
    private final String description;
}