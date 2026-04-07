package com.yhj.his.module.pacs.enums;

import lombok.Getter;

@Getter
public enum ReportStatus {
    DRAFT("草稿", "报告草稿"),
    PENDING_REVIEW("待审核", "报告已提交，等待审核"),
    REJECTED("审核驳回", "审核未通过，需修改"),
    APPROVED("已审核", "审核通过"),
    PUBLISHED("已发布", "报告已发布"),
    PRINTED("已打印", "报告已打印");

    private final String name;
    private final String description;

    ReportStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}