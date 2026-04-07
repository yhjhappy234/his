package com.yhj.his.module.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 检验报告状态枚举
 */
@Getter
@AllArgsConstructor
public enum TestReportStatus {

    DRAFT("草稿", "报告草稿"),
    PENDING_AUDIT("待审核", "等待审核"),
    AUDITED("已审核", "审核通过"),
    PUBLISHED("已发布", "报告已发布"),
    RETURNED("已退回", "报告退回修改");

    private final String code;
    private final String description;
}