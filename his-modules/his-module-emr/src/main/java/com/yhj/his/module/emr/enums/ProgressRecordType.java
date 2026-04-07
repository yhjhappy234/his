package com.yhj.his.module.emr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 病程记录类型枚举
 */
@Getter
@AllArgsConstructor
public enum ProgressRecordType {

    FIRST_PROGRESS("首次病程记录"),
    DAILY_PROGRESS("日常病程记录"),
    CHIEF_ROUND("上级医师查房记录"),
    DIFFICULT_CASE("疑难病例讨论记录"),
    CONSULTATION("会诊记录"),
    TRANSFER("转科记录"),
    OPERATION("手术记录"),
    HANDOVER("交接班记录"),
    PRE_OPERATION("术前讨论记录"),
    POST_OPERATION("术后病程记录");

    private final String description;
}