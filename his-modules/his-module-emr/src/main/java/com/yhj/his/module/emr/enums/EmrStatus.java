package com.yhj.his.module.emr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 病历状态枚举
 */
@Getter
@AllArgsConstructor
public enum EmrStatus {

    DRAFT("草稿"),
    SUBMITTED("已提交"),
    AUDITED("已审核"),
    ARCHIVED("已归档"),
    REJECTED("已退回");

    private final String description;
}