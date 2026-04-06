package com.yhj.his.module.pharmacy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态枚举
 */
@Getter
@AllArgsConstructor
public enum AuditStatus {

    PENDING("待审核", "等待审核"),
    APPROVED("审核通过", "审核通过"),
    REJECTED("审核不通过", "审核不通过");

    private final String name;
    private final String description;
}